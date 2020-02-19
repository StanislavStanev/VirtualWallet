package com.team14.virtualwallet.services;

import com.team14.virtualwallet.models.Picture;
import com.team14.virtualwallet.repositories.PicturesRepository;
import com.team14.virtualwallet.services.contracts.PicturesService;
import com.team14.virtualwallet.utils.ModelFactory;
import org.springframework.stereotype.Service;

@Service
public class PicturesServiceImpl implements PicturesService {

    private static final String DEFAULT_PICTURE_URL = "https://res.cloudinary.com/dqobbbrva/image/upload/v1580487949/lzozyu60vpa0ymkixcco.jpg";

    private PicturesRepository picturesRepository;

    public PicturesServiceImpl(PicturesRepository picturesRepository) {
        this.picturesRepository = picturesRepository;
    }

    @Override
    public Picture create() {
        Picture picture = ModelFactory.createPicture();
        picture.setPictureUrl(DEFAULT_PICTURE_URL);
        return picture;
    }
}
