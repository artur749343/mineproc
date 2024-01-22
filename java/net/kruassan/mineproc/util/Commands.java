package net.kruassan.mineproc.util;

import com.google.common.collect.Lists;
import net.kruassan.mineproc.block.entity.ComputerEntity;
import net.kruassan.mineproc.items.ProcessorItem;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.apache.commons.lang3.ArrayUtils;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.Function;


public class Commands{
    public ComputerEntity computer;
    public Console console;
    public Console_Func[] commands=new Console_Func[]{
            new Console_Func("help", new String[]{}, (args)->{
                console.output("help function");
                return null;
            }
            ),
            new Console_Func("read", new String[]{"int", "int"}, (args)->{
                int i=computer.getStack(console.get_memory()).getOrCreateNbt().getByteArray("mineproc.data").length;
                if ((int)args[1]>=i || (int)args[2]>=i || (int)args[2]<(int)args[1]) {
                    console.output("error: index biggest than maximum");
                    return null;
                }
                    console.read_data((int)args[1], (int)args[2], computer);
                return null;
            },
                    (arg)->(int)arg[2]-(int)arg[1]
            ),
            new Console_Func("write", new String[]{"int", "int", "byte"}, (args)->{
                int i=computer.getStack(console.get_memory()).getOrCreateNbt().getByteArray("mineproc.data").length;
                if ((int)args[1]>=i || (int)args[2]>=i || (int)args[1]>(int)args[2]) {
                    console.output("error: index biggest than maximum");
                    return null;
                } else if ((int)args[2]-(int)args[1]==((byte[])args[3]).length){
                    console.output("error: data size is not correct");
                    return null;
                }
                console.write_data((int)args[1], (int)args[2], (byte[])args[3], computer);
                return null;
            },
                    (arg)->(int)arg[2]-(int)arg[1]
            ),
            new Console_Func("cd", new String[]{"string"}, (args)->{
                for (int i=0;i<4;i++){
                    if (Objects.equals(args[1].toString().split("/")[0], console.All_Disk[i])){
                        console.monitor.path[0]=console.All_Disk[i];
                        break;
                    }
                }
                return null;
            }
            ),
            new Console_Func("clear", new String[]{}, (args)->{
                console.monitor.old_text=Lists.newArrayList();
                return null;
            }
            ),
            new Console_Func("execute", new String[]{"int"}, (args)->{
                int i=computer.getStack(console.get_memory()).getOrCreateNbt().getByteArray("mineproc.data").length;
                if ((int)args[1]>=i){
                    console.output("error: index biggest than maximum");
                    return null;
                }
                execute((int)args[1]);
                return null;
            }
            ),
            new Console_Func("editor", new String[]{}, (args)->{
                console.min_size=0;
                console.text="";
                console.setCursor(0, Screen.hasShiftDown());
                console.start = console.monitor.old_text.size() >= console.getHeight() / 10 ? console.monitor.old_text.size() - console.getHeight() / 10 + 1 : 0;
                console.is_editor=true;
                return null;
            }
            )
    };


    public Commands(ComputerEntity computer, Console console){
        this.computer=computer;
        this.console=console;
    }

    public Object[] Command_executor(String command){
        String[] commands=command.split(" ");

        if (!computer.getStack(4).getOrCreateNbt().contains(ProcessorItem.ITEMS_KEY)){
            console.output("error: processor is not working");
            return null;
        }
        if (!computer.getStack(console.get_memory()).hasNbt()){
            console.output("error: memory is not found");
            return null;
        }
        return execute_command(commands);
    }


    public Object[] execute_command(String[] arguments) {
        Object[] args=new ArrayList<Object>(Arrays.asList(arguments)).toArray();
        for (Console_Func consoleFunc: commands){
            if (Objects.equals(consoleFunc.name, arguments[0])){
                if (consoleFunc.args.length==arguments.length-1) {

                    for (int i=1;i<arguments.length;i++){
                        if (Objects.equals(consoleFunc.args[i-1], "int")){
                            if (!is_int(arguments[i])){
                                console.output(String.format("error: argument %d it is not a number", i));
                                return null;
                            }
                            args[i]=Integer.parseInt(arguments[i]);
                        } else if (Objects.equals(consoleFunc.args[i-1], "byte")){
                            if (!is_byte(arguments[i])){
                                console.output(String.format("error: argument %d it is not a byte", i));
                                return null;
                            }
                            args[i]=string_to_hex(arguments[i]);
                        }
                    }
                    return new Object[]{consoleFunc.get_work_time.apply(args)/ProcessorItem.get_processor_speed(computer.getStack(4)), consoleFunc.function, args};
                } else {
                    console.output(String.format("error: function have %d args but get %d args", consoleFunc.args.length, arguments.length-1));
                    return null;
                }
            }
        }
        return null;
    }


    public static String[] translate(String[] args){
        return translate(args, false);
    }

    public static String[] translate(String[] args, boolean save_contain){
        for (int i=0;i<args.length;i++){
            if (args[i].contains("[")&&args[i].contains("]")){
                String[] r;
                if (save_contain) r = args[i].split("\\[");
                else r = args[i].substring(0, args[i].length() - 1).split("\\[");
                args[i]=r[0];
                args= ArrayUtils.insert(i+1, args, r[1]);
            }
        }
        return args;
    }
    public byte[] assembling(String code){
        byte[] result=new byte[]{};
        Function<String, Boolean> func=str->{return (is_byte(str.substring(0, str.length()-1))&&str.endsWith("h"))||is_int(str);};
        Function<String, byte[]> func1=str->{return (is_int(str)?new byte[]{(byte)Integer.parseInt(str)}:(is_byte(str.substring(0, str.length()-1))&&str.endsWith("h")?string_to_hex(str.substring(0, str.length()-1)):new byte[]{string_to_link(str)}));};
        int[] lines_length={0};
        int[] to_jump={};

        String[] is_if=new String[]{"cml", "cme", "cmb", "cmnl", "cmne", "cmnb"};
        String[] is_operand=new String[]{"or", "and", "nor", "nand", "xor", "add", "sub", "mul", "div"};
        String[] is_operand_1=new String[]{"not", "inc", "dec"};
        for (String line:code.split("\n")){
            line=line.split(";")[0];
            if (Objects.equals(line, "")){
                continue;
            }
            String[] args=line.split(" ");
            if (Objects.equals(args[0], "print")){
                if (args.length==2){
                    result = ArrayUtils.addAll(result, new byte[]{0, (byte)(func.apply(args[1])?0:4), 0, (byte)func1.apply(args[1]).length});
                    result = ArrayUtils.addAll(result, func1.apply(args[1]));
                    lines_length=ArrayUtils.add(lines_length, lines_length[lines_length.length-1]+(func.apply(args[1])?5:(func1.apply(args[1])).length)+4);
                } else {
                    result = ArrayUtils.addAll(result, new byte[]{0, (byte) ((func.apply(args[3]) ? 0 : 4) + (func.apply(args[2]) ? 0 : 2) + (func.apply(args[1]) ? 0 : 1))});
                    result = ArrayUtils.addAll(result, func1.apply(args[1]));
                    result = ArrayUtils.addAll(result, func1.apply(args[2]));
                    result = ArrayUtils.addAll(result, func1.apply(args[3]));
                    lines_length=ArrayUtils.add(lines_length, lines_length[lines_length.length-1]+(func.apply(args[3])?5:(func1.apply(args[3])).length)+4);
                }
            } else if (Objects.equals(args[0], "mov")){
                args=translate(args);
                result=ArrayUtils.addAll(result, new byte[]{1, (byte)((func.apply(args[5])?0:8)+(func.apply(args[4])?0:4)+(func.apply(args[3])?0:2)+(func.apply(args[2])?0:1))});
                result = ArrayUtils.add(result, string_to_link(args[1]));
                result = ArrayUtils.addAll(result, func1.apply(args[2]));
                result=ArrayUtils.addAll(result, func1.apply(args[3]));
                result=ArrayUtils.addAll(result, func1.apply(args[4]));
                result=ArrayUtils.addAll(result, func1.apply(args[5]));
                lines_length=ArrayUtils.add(lines_length, lines_length[lines_length.length-1]+(func.apply(args[4])?7:(func1.apply(args[4])).length)+6);
            } else if (Arrays.asList(is_if).contains(args[0])){
                args=translate(args, true);
                for (int i1=0;i1<6;i1++) {
                    if (Objects.equals(args[0], is_if[i1])) {
                        result = ArrayUtils.add(result, (byte) (i1 + 2));
                        break;
                    }
                }
                if (args.length==5){
                    result=ArrayUtils.add(result, (byte)((func.apply(args[4].substring(0, args[4].length()-1))?0:2)+(func.apply(args[2].substring(0, args[2].length()-1))?0:1)));
                    result = ArrayUtils.addAll(result, func1.apply(args[1]));
                    result = ArrayUtils.addAll(result, func1.apply(args[2].substring(0, args[2].length()-1)));
                    result = ArrayUtils.addAll(result, func1.apply(args[3]));
                    result = ArrayUtils.addAll(result, func1.apply(args[4].substring(0, args[4].length()-1)));
                }else if (args.length==4&&args[2].contains("]")) {
                    result=ArrayUtils.add(result, (byte)((func.apply(args[3])?0:2)+(func.apply(args[2].substring(0, args[2].length()-1))?0:1)));
                    result = ArrayUtils.addAll(result, func1.apply(args[1]));
                    result = ArrayUtils.addAll(result, func1.apply(args[2]));
                    result = ArrayUtils.add(result, (byte)-1);
                    result = ArrayUtils.addAll(result, func1.apply(args[3]));
                } else if (args.length==4&&args[3].contains("]")){
                    result=ArrayUtils.add(result, (byte)((func.apply(args[3].substring(0, args[3].length()-1))?0:2)+(func.apply(args[1])?0:1)));
                    result = ArrayUtils.add(result, (byte)-1);
                    result = ArrayUtils.addAll(result, func1.apply(args[1]));
                    result = ArrayUtils.addAll(result, func1.apply(args[2]));
                    result = ArrayUtils.addAll(result, func1.apply(args[3]));
                } else {
                    result=ArrayUtils.add(result, (byte)((func.apply(args[2])?0:2)+(func.apply(args[1])?0:1)));
                    result = ArrayUtils.add(result, (byte)-1);
                    result = ArrayUtils.addAll(result, func1.apply(args[1]));
                    result = ArrayUtils.add(result, (byte)-1);
                    result = ArrayUtils.addAll(result, func1.apply(args[2]));
                }
                lines_length=ArrayUtils.add(lines_length, lines_length[lines_length.length-1]+6);

            } else if (Objects.equals(args[0], "ret")){
                result=ArrayUtils.add(result, (byte)8);
                lines_length=ArrayUtils.add(lines_length, lines_length[lines_length.length-1]+1);
            }else if (Arrays.asList(is_operand_1).contains(args[0])||(Arrays.asList(is_operand_1).contains(args[0].substring(1))&&args[0].charAt(0)=='m')){
                if (Arrays.asList(is_operand_1).contains(args[0].substring(1))&&args[0].charAt(0)=='m'){
                    args=ArrayUtils.insert(2, args, args[1]);
                }
                args=translate(args, true);
                for (int i1=0;i1<3;i1++) {
                    if (Objects.equals(args[0], is_operand_1[i1])||Objects.equals(args[0].substring(1), is_operand_1[i1])) {
                        result = ArrayUtils.add(result, (byte) (i1+8));
                        break;
                    }
                }

                result = ArrayUtils.addAll(result, func1.apply(args[1]));
                result = ArrayUtils.addAll(result, func1.apply(args[2].substring(0, args[2].length()-1)));
                if (args.length==5){
                    result=ArrayUtils.insert(result.length-2, result, (byte)((func.apply(args[4].substring(0, args[4].length()-1))?0:1)));
                    result = ArrayUtils.addAll(result, func1.apply(args[3]));
                    result = ArrayUtils.addAll(result, func1.apply(args[4].substring(0, args[4].length()-1)));
                } else {
                    result=ArrayUtils.insert(result.length-2, result, (byte)((func.apply(args[3])?0:1)));
                    result = ArrayUtils.add(result, (byte)-1);
                    result = ArrayUtils.addAll(result, func1.apply(args[3]));
                }
                lines_length=ArrayUtils.add(lines_length, lines_length[lines_length.length-1]+6);
            } else if (Arrays.asList(is_operand).contains(args[0])||(Arrays.asList(is_operand).contains(args[0].substring(1))&&args[0].charAt(0)=='m')){
                if (Arrays.asList(is_operand).contains(args[0].substring(1))&&args[0].charAt(0)=='m'){
                    args=ArrayUtils.insert(2, args, args[1]);
                }

                args=translate(args, true);
                for (int i1=0;i1<9;i1++) {
                    if (Objects.equals(args[0], is_operand[i1])||Objects.equals(args[0].substring(1), is_operand[i1])) {
                        result = ArrayUtils.add(result, (byte) (i1+12));
                        break;
                    }
                }

                result = ArrayUtils.addAll(result, func1.apply(args[1]));
                result = ArrayUtils.addAll(result, func1.apply(args[2].substring(0, args[2].length()-1)));
                if (args.length==7){
                    result=ArrayUtils.insert(result.length-2, result, (byte)((func.apply(args[6].substring(0, args[6].length()-1))?0:2)+(func.apply(args[4].substring(0, args[4].length()-1))?0:1)));
                    result = ArrayUtils.addAll(result, func1.apply(args[3]));
                    result = ArrayUtils.addAll(result, func1.apply(args[4].substring(0, args[4].length()-1)));
                    result = ArrayUtils.addAll(result, func1.apply(args[5]));
                    result = ArrayUtils.addAll(result, func1.apply(args[6].substring(0, args[6].length()-1)));
                }else if (args.length==6&&args[4].contains("]")) {
                    result=ArrayUtils.insert(result.length-2, result, (byte)((func.apply(args[5])?0:2)+(func.apply(args[4].substring(0, args[4].length()-1))?0:1)));
                    result = ArrayUtils.addAll(result, func1.apply(args[3]));
                    result = ArrayUtils.addAll(result, func1.apply(args[4].substring(0, args[4].length()-1)));
                    result = ArrayUtils.add(result, (byte)-1);
                    result = ArrayUtils.addAll(result, func1.apply(args[5]));
                } else if (args.length==6&&args[5].contains("]")){
                    result=ArrayUtils.insert(result.length-2, result, (byte)((func.apply(args[5].substring(0, args[5].length()-1))?0:2)+(func.apply(args[3])?0:1)));
                    result = ArrayUtils.add(result, (byte)-1);
                    result = ArrayUtils.addAll(result, func1.apply(args[3]));
                    result = ArrayUtils.addAll(result, func1.apply(args[4]));
                    result = ArrayUtils.addAll(result, func1.apply(args[5].substring(0, args[5].length()-1)));
                } else {
                    result=ArrayUtils.insert(result.length-2, result, (byte)((func.apply(args[4])?0:2)+(func.apply(args[3])?0:1)));
                    result = ArrayUtils.add(result, (byte)-1);
                    result = ArrayUtils.addAll(result, func1.apply(args[3]));
                    result = ArrayUtils.add(result, (byte)-1);
                    result = ArrayUtils.addAll(result, func1.apply(args[4]));
                }
                lines_length=ArrayUtils.add(lines_length, lines_length[lines_length.length-1]+8);
            } else if (Objects.equals(args[0], "jmp")) {
                result=ArrayUtils.addAll(result, (byte)21, (byte)(func.apply(args[1])?0:1));
                to_jump=ArrayUtils.addAll(to_jump, result.length, func.apply(args[1])?b_to_i(func1.apply(args[1])):b_to_i(get_value(func1.apply(args[1]), 0)));
                result = ArrayUtils.addAll(result, func1.apply(args[1]));
                lines_length=ArrayUtils.add(lines_length, lines_length[lines_length.length-1]+3);
            }
        }
        lines_length=ArrayUtils.subarray(lines_length, 0, lines_length.length-1);
        for (int i=0;i<to_jump.length;i+=2){
            result[to_jump[i]]=(byte)(lines_length[to_jump[i+1]]);
        }
        return result;
    }
    public void execute(int start){
        ItemStack item=computer.getStack(console.get_memory());
        NbtCompound nbt=item.getOrCreateNbt();
        int skips=0;
        byte[] result=nbt.getByteArray("mineproc.data");
        for (int i=start;i<result.length;){
            boolean[] type=byte_to_bool_array(result[i+1]);

             if (result[i]==0) {
                 int arg2 = type[1]?b_to_i(get_value(result, i+3)):result[i+3];
                 if (skips>0) skips--;
                 else {
                     int arg1 = type[0]?b_to_i(get_value(result, i+2)):result[i+2];
                     byte[] out=type[2]?Arrays.copyOfRange(get_value(result, i+4), arg1, arg1+arg2) : Arrays.copyOfRange(result, i+4+arg1, i+4+arg1+arg2);
                     for (byte b:out) console.output(b);
                 }
                 i+=type[2]?5:arg2+4;

             }else if (result[i]==1){
                 int arg3 = type[1]?b_to_i(get_value(result, i+4)):result[i + 4];
                 if (skips>0) skips--;
                 else {
                     byte[] arg1 = get_value(result, i+2);
                     int arg2 = type[0]?b_to_i(get_value(result, i+3)):result[i + 3];
                     byte[] arg4 = type[2]?get_value(result, i + 5) : Arrays.copyOfRange(result, i + 5, i+5+arg3);
                     if (type[2]){
                         int arg5 = type[0]?b_to_i(get_value(result, i+6)):result[i + 6];
                         System.arraycopy(arg4, arg5, arg1, arg2, arg3);
                         i++;
                     } else {
                         System.arraycopy(arg4, 0, arg1, arg2, arg3);
                     }
                 }
                 i += type[2]?7:arg3+6;
             }else if (1<result[i]&&result[i]<8){
                 if (0==skips) {

                     int arg1=type[0]?b_to_i(get_value(result, i+3)):result[i+3];
                     int arg2=type[1]?b_to_i(get_value(result, i+5)):result[i+5];
                     if (result[i+2]!=-1) arg1=get_value(result, i+2)[arg1];
                     if (result[i+4]!=-1) arg2=get_value(result, i+4)[arg2];

                     if (result[i]==2) skips+=arg1<arg2?0:1;
                     else if (result[i]==3) skips+=arg1==arg2?0:1;
                     else if (result[i]==4) skips+=arg1>arg2?0:1;
                     else if (result[i]==5) skips+=arg1<arg2?1:0;
                     else if (result[i]==6) skips+=arg1==arg2?1:0;
                     else if (result[i]==7) skips+=arg1>arg2?1:0;
                 } else skips--;
                 i+=6;
             }else if (result[i]==8) {
                 if (0==skips) return;
                 skips--;
                 i++;
             }else if (8<result[i]&&result[i]<12) {
                 if (0==skips) {
                     int arg1 = type[0]?b_to_i(get_value(result, i+5)):result[i+5];
                     if (result[i+4]!=-1) arg1=get_value(result, i+4)[arg1];
                     byte[] data = get_value(result, i+2);
                     int r=0;
                     if (result[i]==8) r=~arg1;
                     if (result[i]==9) r=arg1+1;
                     if (result[i]==10) r=arg1-1;
                     if (arg1< 127) data[result[i+3]] = (byte)(r);
                     else data = Arrays.copyOfRange(ByteBuffer.allocate(4).putInt(r).array(), result[i+3], result[i+3]+4);
                     computer.getStack(result[i+2]+127).getOrCreateNbt().putByteArray("mineproc.data", data);
                 } else skips--;
                 i+=6;
             } else if ((result[i]>11&&result[i]<21)) {
                 if (0==skips) {
                     int arg1=type[0]?b_to_i(get_value(result, i+5)):result[i+5];
                     int arg2=type[1]?b_to_i(get_value(result, i+7)):result[i+7];
                     if (result[i+4]!=-1) arg1=get_value(result, i+4)[arg1];
                     if (result[i+6]!=-1) arg2=get_value(result, i+6)[arg2];
                     int r = 0;
                     if (result[i] == 12) r = arg1|arg2;
                     else if (result[i] == 13) r = arg1&arg2;
                     else if (result[i] == 14) r = ~(arg1|arg2);
                     else if (result[i] == 15) r = ~(arg1&arg2);
                     else if (result[i] == 16) r = arg1^arg2;
                     else if (result[i] == 17) r = arg1+arg2;
                     else if (result[i] == 18) r = arg1-arg2;
                     else if (result[i] == 19) r = arg1*arg2;
                     else if (result[i] == 20) r = arg1/arg2;
                     byte[] data = get_value(result, i+2);
                     if (arg1< 127 && arg2< 127) data[result[i+3]] = (byte) (r);
                     else  data = Arrays.copyOfRange(ByteBuffer.allocate(4).putInt(r).array(), result[i + 3], result[i + 3] + 4);
                     computer.getStack(result[i + 2]+127).getOrCreateNbt().putByteArray("mineproc.data", data);
                 } else skips--;
                 i+=8;
             } else if (result[i]==21){
                 if (0==skips) {
                     i=type[0]?b_to_i(get_value(result, i+2)):result[i+2];
                 } else {
                     skips--;
                     i+=3;
                 }
             } else {
                 console.output("error: no command, in index "+i);
                 return;
             }
        }
    }

    private boolean[] byte_to_bool_array(byte i){
        boolean[] type=new boolean[]{false, false, false, false, false, false, false, false};
        for (int i1=0;i1<8;i1++) {
            type[i1]=i%Math.pow(2, i1+1)>Math.pow(2, i1)-1;
        }
        return type;
    }

    private int b_to_i(byte[] bytes){
        int value=0;
        for (byte b : bytes) {
            value = (value << 8) + (b & 0xFF);
        }
        return value;
    }



    public byte string_to_link(String str){
        if (Objects.equals(str, "i")){
            return -128;
        } else if (Objects.equals(str, "a")){
            return -127;
        } else if (Objects.equals(str, "b")){
            return -126;
        } else if (Objects.equals(str, "c")){
            return -125;
        } else if (Objects.equals(str, "d")){
            return -124;
        }
        return 0;
    }

    public byte[] get_value(byte[] res, int i){
        if (-128==res[i]){
            return i>127?ByteBuffer.allocate(4).putInt(i).array():new byte[]{(byte)i};
        }else if (res[i]<-123) {
            return computer.getStack((int)res[i]+127).getOrCreateNbt().getByteArray("mineproc.data");
        }
        console.output("error: in index "+i);
        return null;
    }


    public static String byte_to_string(byte[] data){
        StringBuilder res= new StringBuilder();
        String[] strings=new String[]{"A", "B", "C", "D", "E", "F"};
        for (byte d : data) {
            int x=d<0?d+256:d;
            res.append(x / 16 > 9 ? strings[x / 16 - 10] : String.valueOf(x / 16)).append(x % 16 > 9 ? strings[x % 16 - 10] : x % 16);
        }
        return res.toString();
    }
    public static boolean is_int(String str){
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public static boolean is_byte(String str){
        for (char c: str.toCharArray()) {
            if (!((47<(byte)c && (byte)c<58) || (96<(byte)c && (byte)c<103) || (64<(byte)c && (byte)c<71))){
                return false;
            }
        }
        return true;
    }
    public static byte[] string_to_hex(String str){
        byte[] result={};
        for (int i=0;i+1<str.length();i+=2) {
            char c=str.charAt(i), c2=str.charAt(i+1);
            int n=0, n2=0;

            if (47<(byte)c && (byte)c<58) n=(byte)c-48;
            else if (96<(byte)c && (byte)c<103) n=(byte)c-87;
            else if (64<(byte)c && (byte)c<71)n=(byte)c-55;

            if (47<(byte)c2 && (byte)c2<58) n2=(byte)c2-48;
            else if (96<(byte)c2 && (byte)c2<103) n2=(byte)c2-87;
            else if (64<(byte)c2 && (byte)c2<71)n2=(byte)c2-55;

            result=ArrayUtils.add(result, (byte)(n*16+n2));
        }
        return result;
    }


}