package demo

import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.NO_CONTENT
import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY

import grails.gorm.transactions.ReadOnly
import grails.gorm.transactions.Transactional

@ReadOnly
class BookController {

    BookService bookService

    static responseFormats = ['json', 'xml']
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond bookService.list(params), model:[bookCount: bookService.count()]
    }

    def show(Long id) {
        println 'CONSTROLLER ' + bookService.get(id).categories
        respond bookService.get(id)
    }

    @Transactional
    def save(Book book) {
        if (book == null) {
            render status: NOT_FOUND
            return
        }
        if (book.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond book.errors
            return
        }

        try {
            bookService.save(book)
        } catch (ValidationException e) {
            respond book.errors
            return
        }

        respond book, [status: CREATED, view:"show"]
    }

    @Transactional
    def update(Book book) {
        if (book == null) {
            render status: NOT_FOUND
            return
        }
        if (book.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond book.errors
            return
        }

        try {
            bookService.save(book)
        } catch (ValidationException e) {
            respond book.errors
            return
        }

        respond book, [status: OK, view:"show"]
    }

    @Transactional
    def delete(Long id) {
        if (id == null) {
            render status: NOT_FOUND
            return
        }

        bookService.delete(id)

        render status: NO_CONTENT
    }
}
