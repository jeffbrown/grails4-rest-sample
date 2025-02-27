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
class PublisherController {

    PublisherService publisherService

    static responseFormats = ['json', 'xml']
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond publisherService.list(params), model:[publisherCount: publisherService.count()]
    }

    def show(Long id) {
        respond publisherService.get(id)
    }

    @Transactional
    def save(Publisher publisher) {
        if (publisher == null) {
            render status: NOT_FOUND
            return
        }
        if (publisher.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond publisher.errors
            return
        }

        try {
            publisherService.save(publisher)
        } catch (ValidationException e) {
            respond publisher.errors
            return
        }

        respond publisher, [status: CREATED, view:"show"]
    }

    @Transactional
    def update(Publisher publisher) {
        if (publisher == null) {
            render status: NOT_FOUND
            return
        }
        if (publisher.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond publisher.errors
            return
        }

        try {
            publisherService.save(publisher)
        } catch (ValidationException e) {
            respond publisher.errors
            return
        }

        respond publisher, [status: OK, view:"show"]
    }

    @Transactional
    def delete(Long id) {
        if (id == null) {
            render status: NOT_FOUND
            return
        }

        publisherService.delete(id)

        render status: NO_CONTENT
    }
}
