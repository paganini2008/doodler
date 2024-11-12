package com.github.doodler.common.jpa;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import org.apache.commons.io.FileUtils;
import com.github.doodler.common.utils.ImageUtils;

public class TestImage {

    public static void main(String[] args) throws Exception {
        FileInputStream fis = FileUtils.openInputStream(new File("D:/sql/验证码/timg11.jpg"));
        FileOutputStream fos = FileUtils.openOutputStream(new File("D:/sql/验证码/timg11_adj.jpg"));
        ImageUtils.rewrite(fis, 512, 512, "jpg", fos);
        fos.flush();
        fos.close();
        fis.close();
        System.out.println("TestImage.main()");
    }

}
