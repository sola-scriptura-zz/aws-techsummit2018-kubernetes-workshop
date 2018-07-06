package hello.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import hello.model.PhotoInfo;

@Repository
public interface PhotoInfoRepository extends JpaRepository<PhotoInfo, Integer> {

}
