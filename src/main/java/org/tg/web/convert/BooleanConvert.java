package org.tg.web.convert;

public class BooleanConvert extends Convert<Boolean>{

    public BooleanConvert(Class<Boolean> type) {
        super(type);
    }

    @Override
    public Object convert(Object arg) throws Exception {

        return defaultConvert(arg.toString());
    }
}
