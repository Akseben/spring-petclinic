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
 * Integration tests for VisitController. Tests GET/POST endpoints for visit management
 * including form display and visit creation with date validation.
 */
@SpringBootTest
class VisitControllerWebTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	// ========== GET /owners/{ownerId}/pets/{petId}/visits/new Tests ==========

	@Test
	void testInitNewVisitForm() throws Exception {
		mockMvc.perform(get("/owners/1/pets/1/visits/new"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"))
			.andExpect(model().attributeExists("visit"));
	}

	@Test
	void testInitNewVisitFormWithOwnerAndPet() throws Exception {
		mockMvc.perform(get("/owners/1/pets/1/visits/new"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("pet"))
			.andExpect(model().attributeExists("owner"));
	}

	// ========== POST /owners/{ownerId}/pets/{petId}/visits/new Tests ==========

	@Test
	void testProcessNewVisitFormSuccess() throws Exception {
		mockMvc
			.perform(post("/owners/1/pets/1/visits/new").param("date", LocalDate.now().toString())
				.param("description", "Regular checkup"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/owners/1"));
	}

	@Test
	void testProcessNewVisitFormSuccessPastDate() throws Exception {
		LocalDate pastDate = LocalDate.now().minusMonths(1);
		mockMvc
			.perform(post("/owners/1/pets/1/visits/new").param("date", pastDate.toString())
				.param("description", "Previous visit"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/owners/1"));
	}

	@Test
	void testProcessNewVisitFormMissingDate() throws Exception {
		mockMvc.perform(post("/owners/1/pets/1/visits/new").param("date", "").param("description", "Regular checkup"))
			.andExpect(status().is3xxRedirection());
	}

	@Test
	void testProcessNewVisitFormMissingDescription() throws Exception {
		mockMvc
			.perform(post("/owners/1/pets/1/visits/new").param("date", LocalDate.now().toString())
				.param("description", ""))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

	@Test
	void testProcessNewVisitFormBlankDescription() throws Exception {
		mockMvc
			.perform(post("/owners/1/pets/1/visits/new").param("date", LocalDate.now().toString())
				.param("description", "   "))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

	@Test
	void testProcessNewVisitFormFutureDate() throws Exception {
		LocalDate futureDate = LocalDate.now().plusDays(1);
		mockMvc
			.perform(post("/owners/1/pets/1/visits/new").param("date", futureDate.toString())
				.param("description", "Future visit"))
			.andExpect(status().is3xxRedirection());
	}

	@Test
	void testProcessNewVisitFormFarFutureDate() throws Exception {
		LocalDate futureDate = LocalDate.now().plusYears(1);
		mockMvc
			.perform(post("/owners/1/pets/1/visits/new").param("date", futureDate.toString())
				.param("description", "Far future visit"))
			.andExpect(status().is3xxRedirection());
	}

	@Test
	void testProcessNewVisitFormInvalidDateFormat() throws Exception {
		mockMvc.perform(post("/owners/1/pets/1/visits/new").param("date", "invalid-date").param("description", "Visit"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

	@Test
	void testProcessNewVisitFormWithLongDescription() throws Exception {
		String longDescription = "This is a detailed visit description with various information about the pet's condition and treatment.";
		mockMvc
			.perform(post("/owners/1/pets/1/visits/new").param("date", LocalDate.now().toString())
				.param("description", longDescription))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/owners/1"));
	}

	@Test
	void testProcessNewVisitFormWithSpecialCharacters() throws Exception {
		mockMvc
			.perform(post("/owners/1/pets/1/visits/new").param("date", LocalDate.now().toString())
				.param("description", "Checkup: teeth cleaning & vaccines"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/owners/1"));
	}

}
