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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for OwnerController. Tests GET/POST endpoints for owner management
 * including display, find, create, and update operations.
 */
@SpringBootTest
class OwnerControllerWebTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private OwnerRepository ownerRepository;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	// ========== GET /owners/{ownerId} - View Owner Tests ==========

	@Test
	void testShowOwner() throws Exception {
		Owner owner = ownerRepository.findById(1).orElseThrow();
		mockMvc.perform(get("/owners/1"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/ownerDetails"))
			.andExpect(model().attributeExists("owner"));
	}

	// ========== GET /owners/find - Find Owner Form Tests ==========

	@Test
	void testInitFindForm() throws Exception {
		mockMvc.perform(get("/owners/find")).andExpect(status().isOk()).andExpect(view().name("owners/findOwners"));
	}

	// ========== GET /owners - Process Find Owner Tests ==========

	@Test
	void testProcessFindFormAllOwners() throws Exception {
		mockMvc.perform(get("/owners")).andExpect(status().isOk()).andExpect(view().name("owners/ownersList"));
	}

	@Test
	void testProcessFindFormByLastName() throws Exception {
		mockMvc.perform(get("/owners").param("lastName", "Franklin")).andExpect(status().is3xxRedirection());
	}

	@Test
	void testProcessFindFormNoResultsFound() throws Exception {
		mockMvc.perform(get("/owners").param("lastName", "NonExistentLastName"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/findOwners"));
	}

	@Test
	void testProcessFindFormMultipleResults() throws Exception {
		mockMvc.perform(get("/owners").param("lastName", ""))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/ownersList"));
	}

	// ========== GET /owners/new - Create Owner Form Tests ==========

	@Test
	void testInitCreationForm() throws Exception {
		mockMvc.perform(get("/owners/new"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/createOrUpdateOwnerForm"))
			.andExpect(model().attributeExists("owner"));
	}

	// ========== POST /owners/new - Process Create Owner Tests ==========

	@Test
	void testProcessCreationFormSuccess() throws Exception {
		mockMvc
			.perform(post("/owners/new").param("firstName", "John")
				.param("lastName", "Doe")
				.param("address", "123 Main St")
				.param("city", "Springfield")
				.param("telephone", "5551234567"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrlPattern("/owners/*"));
	}

	@Test
	void testProcessCreationFormMissingFirstName() throws Exception {
		mockMvc
			.perform(post("/owners/new").param("firstName", "")
				.param("lastName", "Doe")
				.param("address", "123 Main St")
				.param("city", "Springfield")
				.param("telephone", "5551234567"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testProcessCreationFormMissingLastName() throws Exception {
		mockMvc
			.perform(post("/owners/new").param("firstName", "John")
				.param("lastName", "")
				.param("address", "123 Main St")
				.param("city", "Springfield")
				.param("telephone", "5551234567"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testProcessCreationFormMissingAddress() throws Exception {
		mockMvc
			.perform(post("/owners/new").param("firstName", "John")
				.param("lastName", "Doe")
				.param("address", "")
				.param("city", "Springfield")
				.param("telephone", "5551234567"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testProcessCreationFormMissingCity() throws Exception {
		mockMvc
			.perform(post("/owners/new").param("firstName", "John")
				.param("lastName", "Doe")
				.param("address", "123 Main St")
				.param("city", "")
				.param("telephone", "5551234567"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testProcessCreationFormMissingTelephone() throws Exception {
		mockMvc
			.perform(post("/owners/new").param("firstName", "John")
				.param("lastName", "Doe")
				.param("address", "123 Main St")
				.param("city", "Springfield")
				.param("telephone", ""))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testProcessCreationFormInvalidTelephone() throws Exception {
		mockMvc
			.perform(post("/owners/new").param("firstName", "John")
				.param("lastName", "Doe")
				.param("address", "123 Main St")
				.param("city", "Springfield")
				.param("telephone", "invalid"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	// ========== GET /owners/{ownerId}/edit - Update Owner Form Tests ==========

	@Test
	void testInitUpdateForm() throws Exception {
		mockMvc.perform(get("/owners/1/edit"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/createOrUpdateOwnerForm"))
			.andExpect(model().attributeExists("owner"));
	}

	// ========== POST /owners/{ownerId}/edit - Process Update Owner Tests ==========

	@Test
	void testProcessUpdateFormSuccess() throws Exception {
		mockMvc
			.perform(post("/owners/1/edit").param("firstName", "George")
				.param("lastName", "Franklin")
				.param("address", "500 Liberty St")
				.param("city", "Madison")
				.param("telephone", "6085551023"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/owners/1"));
	}

	@Test
	void testProcessUpdateFormMissingFirstName() throws Exception {
		mockMvc
			.perform(post("/owners/1/edit").param("firstName", "")
				.param("lastName", "Franklin")
				.param("address", "500 Liberty St")
				.param("city", "Madison")
				.param("telephone", "6085551023"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testProcessUpdateFormMissingLastName() throws Exception {
		mockMvc
			.perform(post("/owners/1/edit").param("firstName", "George")
				.param("lastName", "")
				.param("address", "500 Liberty St")
				.param("city", "Madison")
				.param("telephone", "6085551023"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testProcessUpdateFormMissingAddress() throws Exception {
		mockMvc
			.perform(post("/owners/1/edit").param("firstName", "George")
				.param("lastName", "Franklin")
				.param("address", "")
				.param("city", "Madison")
				.param("telephone", "6085551023"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testProcessUpdateFormMissingCity() throws Exception {
		mockMvc
			.perform(post("/owners/1/edit").param("firstName", "George")
				.param("lastName", "Franklin")
				.param("address", "500 Liberty St")
				.param("city", "")
				.param("telephone", "6085551023"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testProcessUpdateFormMissingTelephone() throws Exception {
		mockMvc
			.perform(post("/owners/1/edit").param("firstName", "George")
				.param("lastName", "Franklin")
				.param("address", "500 Liberty St")
				.param("city", "Madison")
				.param("telephone", ""))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testProcessUpdateFormInvalidTelephone() throws Exception {
		mockMvc
			.perform(post("/owners/1/edit").param("firstName", "George")
				.param("lastName", "Franklin")
				.param("address", "500 Liberty St")
				.param("city", "Madison")
				.param("telephone", "notaphone"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

}
