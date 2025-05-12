package com.bookstore.repository.impl;

import com.bookstore.exception.DataProcessingException;
import com.bookstore.exception.EntityNotFoundException;
import com.bookstore.model.Book;
import com.bookstore.repository.BookRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepository {
    private final SessionFactory sessionFactory;
    
    @Override
    public Book save(Book book) {
        Session session = null;
        Transaction transaction = null;
        
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.persist(book);
            transaction.commit();
            return book;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DataProcessingException("Cannot save book: " + book, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
    
    @Override
    public Optional<Book> findById(Long id) {
        try (EntityManager entityManager = sessionFactory.createEntityManager()) {
            Book book = entityManager.find(Book.class, id);
            return Optional.ofNullable(book);
        }
    }
    
    @Override
    public List<Book> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("SELECT b FROM Book b", Book.class).getResultList();
        } catch (Exception e) {
            throw new EntityNotFoundException("Cannot find all books", e);
        }
    }
}
