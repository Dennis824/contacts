package com.example.contacts;

import com.example.contacts.pojo.Contact;
import com.example.contacts.repository.ContactRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class ContactsApplicationTests {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	ContactRepository contactRepository;

	private Contact[] contacts = new Contact[] {
			new Contact("1", "John", "4566777"),
			new Contact("2", "Peter", "4657325"),
			new Contact("3", "Homer", "4563732"),
	};

	@BeforeEach
	void setup(){
		for (int i = 0; i < contacts.length; i++) {
			contactRepository.saveContact(contacts[i]);
		}
	}

	@AfterEach
	void clear(){
		contactRepository.getContacts().clear();
	}


	@Test
	public void getContactByIdTest() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/contact/1");
		mockMvc.perform(request)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.name").value(contacts[0].getName()))
				.andExpect(jsonPath("$.phoneNumber").value(contacts[0].getPhoneNumber()));
	}

	@Test
	public void getAllContactsTest() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/contact/all");
		mockMvc.perform(request)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.size()").value(contacts.length))
				.andExpect(jsonPath("$.[?(@.id == \"2\" && @.name == \"Peter\" && @.phoneNumber == \"4657325\")]").exists());
	}

	@Test
	public void validContactCreation() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.post("/contact")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(new Contact("Ra", "98745631469")));

		mockMvc.perform(request).andExpect(status().isCreated());
	}

	@Test
	public void invalidContactCreation() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.post("/contact")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(new Contact("    ", "    ")));

		mockMvc.perform(request).andExpect(status().isBadRequest());
	}

	@Test
	public void contactNotFoundTest() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/contact/4");
		mockMvc.perform(request).andExpect(status().isNotFound());
	}



}