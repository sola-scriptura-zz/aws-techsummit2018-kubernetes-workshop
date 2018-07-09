package hello.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hello.model.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {

}