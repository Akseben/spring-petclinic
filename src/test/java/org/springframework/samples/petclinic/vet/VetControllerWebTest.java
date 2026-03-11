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
package org.springframework.samples.petclinic.vet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for VetController. Tests GET endpoints for veterinarian listing in
 * multiple content formats (HTML, JSON, XML).
 */
@SpringBootTest
class VetControllerWebTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	// ========== GET /vets Tests - Multiple Content Type Support ==========

	@Test
	void testShowVeterinariansHtml() throws Exception {
		mockMvc.perform(get("/vets.html"))
			.andExpect(status().isOk())
			.andExpect(view().name("vets/vetList"))
			.andExpect(model().attributeExists("listVets", "currentPage", "totalPages"));
	}

	@Test
	void testShowVeterinariansHtmlWithAcceptHeader() throws Exception {
		mockMvc.perform(get("/vets.html").accept(MediaType.TEXT_HTML))
			.andExpect(status().isOk())
			.andExpect(view().name("vets/vetList"))
			.andExpect(model().attributeExists("listVets"));
	}

	@Test
	void testShowVeterinariansJson() throws Exception {
		mockMvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}

	@Test
	void testShowVeterinariansJsonContainsVets() throws Exception {
		mockMvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.vetList").isArray());
	}

	@Test
	void testShowVeterinariansXml() throws Exception {
		mockMvc.perform(get("/vets").accept(MediaType.APPLICATION_XML))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_XML));
	}

	@Test
	void testShowVeterinariansXmlContainsVets() throws Exception {
		mockMvc.perform(get("/vets").accept(MediaType.APPLICATION_XML))
			.andExpect(status().isOk())
			.andExpect(xpath("//vets").exists());
	}

	@Test
	void testShowVeterinariansWithoutExtension() throws Exception {
		mockMvc.perform(get("/vets"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}

	@Test
	void testShowVeterinariansJsonResponseStructure() throws Exception {
		mockMvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.vetList[0].id").isNumber())
			.andExpect(jsonPath("$.vetList[0].firstName").isString());
	}

	@Test
	void testShowVeterinariansJsonNotEmpty() throws Exception {
		mockMvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.vetList[0]").exists());
	}

	@Test
	void testShowVeterinariansMultipleFormats() throws Exception {
		// Test HTML
		mockMvc.perform(get("/vets.html")).andExpect(status().isOk());

		// Test JSON
		mockMvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

		// Test XML
		mockMvc.perform(get("/vets").accept(MediaType.APPLICATION_XML)).andExpect(status().isOk());
	}

}
