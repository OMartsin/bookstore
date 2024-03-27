package dev.martsin.bookstore.repositories;

import dev.martsin.bookstore.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findById(Long id);
    List<Book> findAll();
    Book save(Book book);
    void deleteById(Long id);

}