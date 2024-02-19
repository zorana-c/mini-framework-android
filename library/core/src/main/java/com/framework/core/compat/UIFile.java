package com.framework.core.compat;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * @Author create by Zhengzelong on 2022/10/17
 * @Email : 171905184@qq.com
 * @Description :
 */
public class UIFile {
    public static final int TYPE_B = 1; // 获取文件大小单位为B的double值
    public static final int TYPE_KB = 2; // 获取文件大小单位为KB的double值
    public static final int TYPE_MB = 3; // 获取文件大小单位为MB的double值
    public static final int TYPE_GB = 4; // 获取文件大小单位为GB的double值

    private static final String DIR_FILE = "files";
    private static final String DIR_AUDIO = "audios";
    private static final String DIR_VIDEO = "videos";
    private static final String DIR_IMAGE = "images";

    private static String rootPath;

    public static void init(@NonNull Context context) {
        rootPath = rootPath(context);
    }

    public static boolean isSdcardExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    @NonNull
    public static String getRootPath() {
        final String rootPath = UIFile.rootPath;
        if (TextUtils.isEmpty(rootPath)) {
            throw new IllegalStateException("Unknown init");
        }
        return rootPath;
    }

    @NonNull
    public static String getFilePath() {
        return getChildPath(DIR_FILE);
    }

    @NonNull
    public static String getAudioPath() {
        return getChildPath(DIR_AUDIO);
    }

    @NonNull
    public static String getVideoPath() {
        return getChildPath(DIR_VIDEO);
    }

    @NonNull
    public static String getImagePath() {
        return getChildPath(DIR_IMAGE);
    }

    @NonNull
    public static String getChildPath(@NonNull String dirName) {
        final File childFile = new File(getRootPath(), dirName);
        if (!childFile.mkdir()) {
            childFile.mkdirs();
        }
        return childFile.getAbsolutePath();
    }

    // 文件目录下文件随APK卸掉而移除
    @NonNull
    private static String rootPath(@NonNull Context context) {
        File dir = null;
        try {
            if (isSdcardExist()) {
                dir = context.getExternalCacheDir();
            }
            if (dir == null) {
                dir = context.getCacheDir();
            }
        } catch (@NonNull Exception tr) {
            dir = context.getFilesDir();
        }
        if (!dir.mkdir()) {
            dir.mkdirs();
        }
        return dir.getAbsolutePath();
    }

    /**
     * 获取文件/文件夹单位大小
     *
     * @param file 文件/文件夹路径
     */
    public static long getFileSize(@Nullable File file) throws IOException {
        if (file == null || !file.exists()) {
            return 0L;
        }
        long fileSize = 0L;
        if (file.isDirectory()) {
            final File[] files = file.listFiles();
            if (files == null) {
                return fileSize;
            }
            for (File childFile : files) {
                fileSize += getFileSize(childFile);
            }
        } else {
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(file);
                fileSize = fileInputStream.available();
            } finally {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            }
        }
        return fileSize;
    }

    /**
     * 获取文件/文件夹单位大小
     *
     * @param file 文件/文件夹路径
     * @param type 获取大小的类型
     * @see UIFile#TYPE_B
     * @see UIFile#TYPE_KB
     * @see UIFile#TYPE_MB
     * @see UIFile#TYPE_GB
     */
    public static double getFileSize(@Nullable File file, int type) {
        long blockFileSize = 0L;
        try {
            blockFileSize = getFileSize(file);
        } catch (@NonNull IOException tr) {
            tr.printStackTrace();
        }
        return formatFileSize(blockFileSize, type);
    }

    /**
     * 转换文件/文件夹大小格式(指定转换类型)
     *
     * @param fileSizeLong 文件/文件夹的大小
     * @param type         指定大小的类型
     * @see UIFile#TYPE_B
     * @see UIFile#TYPE_KB
     * @see UIFile#TYPE_MB
     * @see UIFile#TYPE_GB
     */
    public static double formatFileSize(long fileSizeLong, int type) {
        final DecimalFormat decimalFormat = new DecimalFormat("#.00");
        double fileSize = 0D;
        switch (type) {
            case TYPE_B:
                fileSize = Double.parseDouble(decimalFormat.format((double) fileSizeLong));
                break;
            case TYPE_KB:
                fileSize = Double.parseDouble(decimalFormat.format((double) fileSizeLong / 1024D));
                break;
            case TYPE_MB:
                fileSize = Double.parseDouble(decimalFormat.format((double) fileSizeLong / 1048576D));
                break;
            case TYPE_GB:
                fileSize = Double.parseDouble(decimalFormat.format((double) fileSizeLong / 1073741824D));
                break;
        }
        return fileSize;
    }

    /**
     * 转换文件/文件夹大小格式
     */
    @NonNull
    public static String formatFileSizeString(long fileSizeLong) {
        final DecimalFormat decimalFormat = new DecimalFormat("#.00");
        final String fileSizeString;
        if (fileSizeLong < 1024) {
            fileSizeString = decimalFormat.format((double) fileSizeLong) + "B";
        } else if (fileSizeLong < 1048576) {
            fileSizeString = decimalFormat.format((double) fileSizeLong / 1024) + "KB";
        } else if (fileSizeLong < 1073741824) {
            fileSizeString = decimalFormat.format((double) fileSizeLong / 1048576) + "MB";
        } else {
            fileSizeString = decimalFormat.format((double) fileSizeLong / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    public static boolean clearCaches() {
        return deleteFolderFile(new File(getRootPath()));
    }

    public static boolean deleteFolderFile(@Nullable File file) {
        return deleteFolderFile(file, false);
    }

    public static boolean deleteFolderFile(@Nullable File file, boolean deleteThisPath) {
        if (file == null || !file.exists()) {
            return false;
        }
        try {
            if (file.isDirectory()) {
                final File[] files = file.listFiles();
                if (files != null) {
                    for (File childFile : files) {
                        deleteFolderFile(childFile, true);
                    }
                }
            }
            if (deleteThisPath) {
                if (file.isDirectory()) {
                    final File[] files = file.listFiles();
                    if (files != null && files.length > 0) {
                        return true;
                    }
                }
                file.delete();
            }
            return true;
        } catch (@NonNull Exception tr) {
            tr.printStackTrace();
            return false;
        }
    }

    @Nullable
    public static File uri2File(@NonNull Context context, @Nullable Uri it) {
        if (it == null) {
            return null;
        }
        final String[] projection = {MediaStore.Images.Media.DATA};
        final Cursor c;
        c = context.getContentResolver()
                .query(it, projection, null, null, null);
        if (c != null && c.moveToFirst()) {
            final int ci = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            final String fPath = c.getString(ci);
            c.close();
            return new File(fPath);
        }
        return null;
    }
}
