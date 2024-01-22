package net.kruassan.mineproc.util;

import com.google.common.base.Function;

public class Console_Func{
    String name;
    String[] args;
    Function<Object[], Void> function;
    Function<Object[], Integer> get_work_time;

    public Console_Func(String name, String[] args, Function<Object[], Void> function){
        this.name=name;
        this.args=args;
        this.function=function;
        this.get_work_time=(arg)->{return 0;};
    }

    public Console_Func(String name, String[] args, Function<Object[], Void> function, int get_work_time){
        this.name=name;
        this.args=args;
        this.function=function;
        this.get_work_time=(arg)->{return get_work_time;};
    }

    public Console_Func(String name, String[] args, Function<Object[], Void> function, Function<Object[], Integer> get_work_time){
        this.name=name;
        this.args=args;
        this.function=function;
        this.get_work_time=get_work_time;
    }
}