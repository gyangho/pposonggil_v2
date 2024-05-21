package pposonggil.usedStuff.repository.image.custom;

import pposonggil.usedStuff.domain.Image;

import java.util.List;

public interface CustomImageRepository {
    List<Image> findAllWithBoard();
}
