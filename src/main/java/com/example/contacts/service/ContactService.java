package com.example.contacts.service;

import com.example.contacts.pojo.Contact;

import java.util.List;

public interface ContactService {
    Contact getContactById(String id);
    void saveContact(Contact contact);
    List<Contact> getContacts();
}