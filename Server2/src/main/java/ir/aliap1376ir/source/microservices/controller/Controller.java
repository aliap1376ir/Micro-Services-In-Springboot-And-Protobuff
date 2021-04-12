package ir.aliap1376ir.source.microservices.controller;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.ClientCallStreamObserver;
import ir.aliap1376ir.source.microservices.models.db.Book;
import ir.aliap1376ir.source.microservices.models.db.BookRepository;
import ir.aliap1376ir.source.microservices.models.db.Category;
import ir.aliap1376ir.source.microservices.models.transfer.CategoryServiceGrpc;
import ir.aliap1376ir.source.microservices.models.transfer.Models;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Nullable;
import java.util.ArrayList;

@RestController
public class Controller {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private EurekaClient client;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping(path = "/books")
    private void init() {
        newData("شاهزاده کوچولو", 1);
        newData("رمان۱", 2);
    }

    private void newData(String name, long category) {
        Book book = new Book();
        book.setName(name);
        book.setCategoryId(category);
        bookRepository.save(book);
    }


    @PostMapping(path = "/books/new/json")
    private Book newBookJson(@RequestBody Book bookJson) {
        bookJson = bookRepository.save(bookJson);
        return bookJson;
    }

    @PostMapping(path = "/books/new/proto")
    private Models.Book newBookProto(@RequestBody Models.Book bookProto) {

        Book bookJson = new Book();
        bookJson.setId(bookProto.getId());
        bookJson.setName(bookProto.getName());
        bookJson.setCategoryId(bookProto.getCategory().getId());

        bookJson = bookRepository.save(bookJson);

        Models.Category categoryProto = restTemplate.getForObject("http://categories/categories/categories/findById?id=" + bookJson.getCategoryId(), Models.Category.class);

        bookProto = Models.Book.newBuilder()
                .setId(bookJson.getId())
                .setName(bookJson.getName())
                .setCategory(categoryProto)
                .build();

        return bookProto;
    }


    @GetMapping(path = "/books/all/json")
    private ArrayList<Book> getAllCategoriesJson() {
        ArrayList<Book> books = bookRepository.findAll();

        for (Book bookJson : books) {

/*            Models.Category categoryProto = restTemplate.getForObject("http://categories/categories/categories/findById?id=" + bookJson.getCategoryId(), Models.Category.class);

            if (categoryProto != null) {
                Category categoryJson = new Category();
                categoryJson.setId(categoryProto.getId());
                categoryJson.setName(categoryProto.getName());


                bookJson.setCategory(categoryJson);
            }*/


            final InstanceInfo instanceInfo = client.getNextServerFromEureka("categories", false);
            System.out.println(instanceInfo.getIPAddr() + ":" + instanceInfo.getPort());
            final ManagedChannel channel = ManagedChannelBuilder.forAddress(instanceInfo.getIPAddr(), instanceInfo.getPort()+1)
//            final ManagedChannel channel = ManagedChannelBuilder.forAddress(instanceInfo.getIPAddr(), 6565)
                    .usePlaintext()
                    .build();
            final CategoryServiceGrpc.CategoryServiceBlockingStub blockingStub = CategoryServiceGrpc.newBlockingStub(channel);

            Models.RequestParam request = Models.RequestParam.newBuilder().setId(bookJson.getCategoryId()).build();

            Models.Category categoryProto =blockingStub.findById(request);

           if(categoryProto != null){
                Category categoryJson = new Category();
                categoryJson.setId(categoryProto.getId());
                categoryJson.setName(categoryProto.getName());


                bookJson.setCategory(categoryJson);
            }


        }

        return books;
    }

    @GetMapping(path = "/books/all/proto")
    private Models.Books getAllBooksProto() {
        Models.Books booksProto = Models.Books.newBuilder().build();

        ArrayList<Book> booksJson = bookRepository.findAll();
        for (Book bookJson : booksJson) {

            Models.Category categoryProto = restTemplate.getForObject("http://categories/categories/categories/findById?id=" + bookJson.getCategoryId(), Models.Category.class);

            if (categoryProto != null) {

                Models.Book bookProto = Models.Book.newBuilder()
                        .setId(bookJson.getId())
                        .setName(bookJson.getName())
                        .setCategory(categoryProto)
                        .build();

                booksProto = booksProto.toBuilder().addArray(bookProto).build();
            }
        }

        return booksProto;
    }

}