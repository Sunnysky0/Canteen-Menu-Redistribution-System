package cn.sunnysky.util;

import android.content.Context;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class AndroidClassUtil extends ClassUtil{
    private Context ctx;


    public AndroidClassUtil(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public List<Class<?>> getClassesForPackage(String pkgName) throws IOException, URISyntaxException {
        return ClassReader.read(pkgName,ctx);
    }
}
