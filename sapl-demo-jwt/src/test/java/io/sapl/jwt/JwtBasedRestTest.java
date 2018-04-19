package io.sapl.jwt;

import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.util.JacksonJsonParser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class JwtBasedRestTest {

	@LocalServerPort
	private int port;

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void tokenAuthSucceeds() {
		assertEquals("Token Authentication was not successful.", HttpStatus.OK,
				new TestRestTemplate("testingClient", "secret")
						.getForEntity("http://localhost:" + port + "/oauth/token_key", String.class).getStatusCode());
	}

	@Test
	public void callDiagnosisMethodSucceeds() throws Exception {
		String accessToken = obtainAccessToken("Thomas", "password");
		mockMvc.perform(get("/person/readDiag/1").header("Authorization", "Bearer " + accessToken)
				.accept(MediaType.APPLICATION_JSON_UTF8)).andExpect(status().isOk());
	}

	@Test
	public void callDeleteMethodSucceeds() throws Exception {
		String accessToken = obtainAccessToken("Thomas", "password");
		mockMvc.perform(delete("/person/1").header("Authorization", "Bearer " + accessToken)
				.accept(MediaType.APPLICATION_JSON_UTF8)).andExpect(status().isForbidden());
	}

	private String obtainAccessToken(String username, String password) throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "password");
		params.add("client_id", "testingClient");
		params.add("username", username);
		params.add("password", password);
		ResultActions result = mockMvc
				.perform(post("/oauth/token").params(params).with(httpBasic("testingClient", "secret"))
						.accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));

		String resultString = result.andReturn().getResponse().getContentAsString();

		JacksonJsonParser jsonParser = new JacksonJsonParser();
		return jsonParser.parseMap(resultString).get("access_token").toString();
	}

}
