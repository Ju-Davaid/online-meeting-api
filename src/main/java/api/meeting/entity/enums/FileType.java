package api.meeting.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件类型枚举
 */
@Getter
@AllArgsConstructor
public enum FileType {

    // 枚举常量定义 (按照截图风格：type, 后缀数组, 主后缀, 描述)
    IMAGE(0, new String[]{"jpeg", "jpg", "png", "gif", "bmp", "webp"}, ".jpg", "图片"),
    VIDEO(1, new String[]{"mp4", "avi", "rmvb", "mkv", "mov"}, ".mp4", "视频"),
    AUDIO(2, new String[]{"mp3", "wav", "flac", "aac", "ogg"}, ".mp3", "音频"),
    DOC(3, new String[]{"doc", "docx", "xls", "xlsx", "ppt", "pptx", "pdf", "txt"}, ".doc", "文档"),
    OTHER(4, new String[]{"zip", "rar", "7z", "tar", "gz"}, ".zip", "其他文件");

    private final Integer type;
    private final String[] suffixArray;
    private final String suffix;
    private final String desc;


    /**
     * 根据 type 获取对应的枚举
     *
     * @param type 文件类型编号
     * @return 对应的枚举对象，未找到返回 null
     */
    public static FileType getByType(Integer type) {
        if (null == type) {
            return null;
        }
        for (FileType typeEnum : FileType.values()) {
            if (typeEnum.getType().equals(type)) {
                return typeEnum;
            }
        }
        return null;
    }

    /**
     * 根据文件后缀名获取枚举 (建议补充，开发中非常常用)
     * 例如传入 "jpg" 或 "JPG" 返回 IMAGE
     *
     * @param fileSuffix 文件后缀名
     * @return 对应的枚举对象，未找到返回 OTHER
     */
    public static FileType getBySuffix(String fileSuffix) {
        if (fileSuffix == null || fileSuffix.trim().isEmpty()) {
            return OTHER;
        }
        String cleanSuffix = fileSuffix.startsWith(".") ? fileSuffix.substring(1) : fileSuffix;
        cleanSuffix = cleanSuffix.toLowerCase();

        for (FileType typeEnum : FileType.values()) {
            for (String suffix : typeEnum.getSuffixArray()) {
                if (suffix.equalsIgnoreCase(cleanSuffix)) {
                    return typeEnum;
                }
            }
        }
        return OTHER;
    }
}