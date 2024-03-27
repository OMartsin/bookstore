package dev.martsin.bookstore.mappers;

import com.example.grpc.book.BookID;
import com.example.grpc.book.BookRequest;
import com.example.grpc.book.BookResponse;
import dev.martsin.bookstore.models.Book;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookMapper {
    BookResponse toBookResponse(Book book);
    BookID toBookID(Book book);
    Book toBook(BookRequest bookRequest);
}
