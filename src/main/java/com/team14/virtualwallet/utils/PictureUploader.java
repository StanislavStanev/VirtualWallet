package com.team14.virtualwallet.utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import com.team14.virtualwallet.models.Picture;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class PictureUploader {

    private final static Cloudinary cloudinary;

    static {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dqobbbrva",
                "api_key", "577144674377792",
                "api_secret", "R7j6MoVINvqljefhVNcWbf34nDI"));
    }

    public String uploadPicture(MultipartFile filePart) throws IOException {
        String randomUUID = UUID.randomUUID().toString();
        String outputFile = "C:\\Users\\Stanislav\\Desktop\\FINAL_PROJECT_BANICA\\virtual-wallet\\src\\main\\resources\\images\\" + randomUUID + ".jpeg";

        InputStream inputStream = filePart.getInputStream();
        Files.copy(inputStream, Paths.get(outputFile));
        File file = new File(outputFile);

        Map uploadResult = cloudinary.uploader().upload(file, new HashMap());

        String pictureLink = (String) uploadResult.get("url");
//        pictureLink = pictureLink.substring(0,49) + "w_900,h_600,c_scale/" + pictureLink.substring(49);

        return pictureLink;
    }
}
