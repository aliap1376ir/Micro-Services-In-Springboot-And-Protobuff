package ir.aliap1376ir.source.microservices.models.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Book findByName(String name);

    ArrayList<Book> findAll();
}