package io.linuxserver.davos.schedule.workflow.actions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class HttpAPICallActionTest {

    @InjectMocks
    private HttpAPICallAction httpAPICallAction;

    @Mock
    private RestTemplate mockRestTemplate;

    @Captor
    private ArgumentCaptor<HttpEntity<String>> entityCaptor;

    @Before
    public void setUp() {

        httpAPICallAction = new HttpAPICallAction("http://url", HttpMethod.POST, "application/json", "{\"hello\":\"world\"}");

        initMocks(this);
    }

    @Test
    public void shouldCallRestTemplateWithCorrectParams() {

        PostDownloadExecution execution = new PostDownloadExecution();
        execution.fileName = "file.txt";

        httpAPICallAction.execute(execution);

        verify(mockRestTemplate).exchange(eq("http://url"), eq(HttpMethod.POST), entityCaptor.capture(), eq(Object.class));

        String body = entityCaptor.getValue().getBody();

        assertThat(body).isEqualTo("{\"hello\":\"world\"}");
    }

    @Test
    public void postDataShouldHaveCorrectHeaderValue() {

        PostDownloadExecution execution = new PostDownloadExecution();
        execution.fileName = "filename";

        httpAPICallAction.execute(execution);

        verify(mockRestTemplate).exchange(eq("http://url"), eq(HttpMethod.POST), entityCaptor.capture(), eq(Object.class));

        HttpHeaders headers = entityCaptor.getValue().getHeaders();

        assertThat(headers.getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }

    @Test
    public void ifRestTemplateFailsThenDoNothing() {

        when(mockRestTemplate.exchange(eq("http://url"), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
                .thenThrow(new RestClientException(""));

        httpAPICallAction.execute(new PostDownloadExecution());
    }

    @Test
    public void ifRestTemplateFailsBecauseMessageIsUnreadbleThenDoNothing() {

        when(mockRestTemplate.exchange(eq("http://url"), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
                .thenThrow(new HttpMessageConversionException(""));

        httpAPICallAction.execute(new PostDownloadExecution());
    }
}
