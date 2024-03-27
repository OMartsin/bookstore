package dev.martsin.bookstore;

import com.example.grpc.book.*;
import com.google.protobuf.Empty;
import dev.martsin.bookstore.mappers.BookMapper;
import dev.martsin.bookstore.models.Book;
import dev.martsin.bookstore.repositories.BookRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.Optional;

@GrpcService
@RequiredArgsConstructor
public class BookService extends BookServiceGrpc.BookServiceImplBase {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public void getBook(BookID request, StreamObserver<BookResponse> responseObserver) {
        Optional<Book> bookOptional;
        try{
            bookOptional = bookRepository.findById((long) request.getId());
        }
        catch (NumberFormatException e){
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT
                    .withDescription("Invalid book id").asRuntimeException());
            return;
        }
        if (bookOptional.isEmpty()) {
            responseObserver.onError(io.grpc.Status.NOT_FOUND
                    .withDescription("Book not found").asRuntimeException());
            return;
        }
        responseObserver.onNext(bookMapper.toBookResponse(bookOptional.get()));
        responseObserver.onCompleted();
    }

    @Override
    public void addBook(BookRequest request, StreamObserver<BookID> responseObserver) {
        Book book = bookMapper.toBook(request);
        try{
            book.setId(null);
            book = bookRepository.save(book);
        }
        catch (Exception e){
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT
                    .withDescription("Invalid book data").asRuntimeException());
            return;
        }
        responseObserver.onNext(bookMapper.toBookID(book));
        responseObserver.onCompleted();
    }

    @Override
    public void updateBook(BookRequest request, StreamObserver<BookResponse> responseObserver) {
        Book book = bookMapper.toBook(request);
        try{
            var bookOptional = bookRepository.findById(book.getId());
            if (bookOptional.isEmpty()) {
                responseObserver.onError(io.grpc.Status.NOT_FOUND
                        .withDescription("Book not found").asRuntimeException());
                return;
            }
            book = bookRepository.save(book);
        }
        catch (Exception e){
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT
                    .withDescription("Invalid book data").asRuntimeException());
            return;
        }
        responseObserver.onNext(bookMapper.toBookResponse(book));
        responseObserver.onCompleted();
    }

    @Override
    public void deleteBook(BookID request, StreamObserver<Empty> responseObserver) {
        try{
            bookRepository.deleteById((long) request.getId());
        }
        catch (NumberFormatException e){
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT
                    .withDescription("Invalid book id").asRuntimeException());
            return;
        }
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void listBooks(Empty request, StreamObserver<BookList> responseObserver) {
        BookList.Builder bookListBuilder = BookList.newBuilder();
        bookRepository.findAll().forEach(book -> bookListBuilder.addBooks(bookMapper.toBookResponse(book)));
        responseObserver.onNext(bookListBuilder.build());
        responseObserver.onCompleted();
    }
}
