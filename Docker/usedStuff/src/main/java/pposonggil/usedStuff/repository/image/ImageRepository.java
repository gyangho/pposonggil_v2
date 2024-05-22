package pposonggil.usedStuff.repository.image;

import org.springframework.data.jpa.repository.JpaRepository;
import pposonggil.usedStuff.domain.Image;
import pposonggil.usedStuff.repository.image.custom.CustomImageRepository;

public interface ImageRepository extends JpaRepository<Image, Long>, CustomImageRepository {
}
