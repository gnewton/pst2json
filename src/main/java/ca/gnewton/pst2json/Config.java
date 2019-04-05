package ca.gnewton.pst2json;

public class Config{
    public static Outputs output = Outputs.XML;
    //public static boolean base64BodyEncode = true;
    public static boolean noBase64Encode = false;
    public static boolean gzipOut = false;
    public static boolean extractTextFromAttachments = true;
    public static boolean includeAttachmentMetadata = true;
    public static boolean includeAttachments = true;
}
