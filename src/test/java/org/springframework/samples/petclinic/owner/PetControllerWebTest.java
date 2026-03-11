/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.owner;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import jakarta.servlet.ServletException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for PetController using Spring Boot Test framework. Tests GET/POST
 * endpoints for pet management including creation and updates.
 */
@SpringBootTest
class PetControllerWebTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private OwnerRepository ownerRepository;

	@Autowired
	private PetTypeRepository petTypeRepository;

	@org.junit.jupiter.api.BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	void testInitCreationForm() throws Exception {
		Owner owner = ownerRepository.findById(1).orElseThrow();
		mockMvc.perform(get("/owners/1/pets/new"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	@Test
	void testProcessCreationFormSuccess() throws Exception {
		mockMvc
			.perform(post("/owners/1/pets/new").param("name", "NewPet")
				.param("birthDate", "2023-01-01")
				.param("type", "dog"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/owners/1"));
	}

	@Test
	void testProcessCreationFormMissingName() throws Exception {
		mockMvc
			.perform(post("/owners/1/pets/new").param("name", "").param("birthDate", "2023-01-01").param("type", "dog"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdatePetForm"))
			.andExpect(model().hasErrors());
	}

	@Test
	void testProcessCreationFormFutureBirthDate() throws Exception {
		LocalDate futureDate = LocalDate.now().plusDays(1);
		mockMvc
			.perform(post("/owners/1/pets/new").param("name", "TestPet")
				.param("birthDate", futureDate.toString())
				.param("type", "dog"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdatePetForm"))
			.andExpect(model().hasErrors());
	}

	@Test
	void testInitUpdateForm() throws Exception {
		mockMvc.perform(get("/owners/1/pets/1/edit"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	@Test
	void testProcessUpdateFormSuccess() throws Exception {
		mockMvc
			.perform(post("/owners/1/pets/1/edit").param("id", "1")
				.param("name", "UpdatedPet")
				.param("birthDate", "2020-01-01")
				.param("type", "dog"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/owners/1"));
	}

	@Test
	void testProcessUpdateFormMissingName() throws Exception {
		mockMvc
			.perform(post("/owners/1/pets/1/edit").param("id", "1")
				.param("name", "")
				.param("birthDate", "2020-01-01")
				.param("type", "dog"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	@Test
	void testProcessUpdateFormFutureBirthDate() throws Exception {
		LocalDate futureDate = LocalDate.now().plusDays(1);
		mockMvc
			.perform(post("/owners/1/pets/1/edit").param("id", "1")
				.param("name", "UpdatedName")
				.param("birthDate", futureDate.toString())
				.param("type", "dog"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	// ========== Exception Handling Tests - Invalid IDs ==========

	@Test
	void testInitCreationFormWithInvalidOwnerId() throws Exception {
		// Triggered through lambda exception handler in PetController.findOwner()
		assertThrows(ServletException.class, () -> mockMvc.perform(get("/owners/9999/pets/new")));
	}

	@Test
	void testInitUpdateFormWithInvalidOwnerId() throws Exception {
		// Triggered through lambda exception handler in PetController.findOwner()
		assertThrows(ServletException.class, () -> mockMvc.perform(get("/owners/9999/pets/1/edit")));
	}

	@Test
	void testInitUpdateFormWithInvalidPetId() throws Exception {
		// Triggered through lambda exception handler in PetController.findPet()
		assertThrows(ServletException.class, () -> mockMvc.perform(get("/owners/1/pets/9999/edit")));
	}

	@Test
	void testProcessCreationFormWithInvalidOwnerId() throws Exception {
		// Triggered through lambda exception handler in PetController.findOwner()
		assertThrows(ServletException.class,
				() -> mockMvc.perform(post("/owners/9999/pets/new").param("name", "Buddy")
					.param("birthDate", "2020-01-01")
					.param("type", "dog")));
	}

	@Test
	void testProcessUpdateFormWithInvalidOwnerId() throws Exception {
		// Triggered through lambda exception handler in PetController.findOwner()
		assertThrows(ServletException.class,
				() -> mockMvc.perform(post("/owners/9999/pets/1/edit").param("id", "1")
					.param("name", "UpdatedName")
					.param("birthDate", "2020-01-01")
					.param("type", "dog")));
	}

	@Test
	void testProcessUpdateFormWithInvalidPetId() throws Exception {
		// Triggered through lambda exception handler in PetController.findPet()
		assertThrows(ServletException.class,
				() -> mockMvc.perform(post("/owners/1/pets/9999/edit").param("id", "9999")
					.param("name", "UpdatedName")
					.param("birthDate", "2020-01-01")
					.param("type", "dog")));
	}

}
