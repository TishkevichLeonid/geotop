package com.geotop.geotopproject;


import com.geotop.geotopproject.dao.impl.PlaceRepositoryImpl;
import com.geotop.geotopproject.loader.helper.SentimentAnalyser;
import com.geotop.geotopproject.model.places.Checkin;
import com.geotop.geotopproject.model.places.Place;
import com.geotop.geotopproject.service.LoaderService;
import com.geotop.geotopproject.service.PlaceService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GeotopProjectApplicationTests {

	@InjectMocks
	private SentimentAnalyser analyser;

	@Mock
	private PlaceService placeService;

	@Mock
	private LoaderService loaderService;

	@Mock
	private PlaceRepositoryImpl placeRepository;

	@Test
	public void contextLoads() {
		List<Checkin> checkins = Collections.emptyList();
		String message = analyser.ratePlace(checkins);
		Assert.assertEquals(null, message);
	}

	@Test
	public void placeService_interactWithDAO() throws NullPointerException {
		when(placeRepository.findById(anyString())).thenReturn(null);
		doThrow(new RuntimeException()).when(placeRepository).findById(anyString());
	}

	@Test
	public void placeServiceTest(){
		when(placeService.getPlaceById(anyString())).thenReturn(null);
		when(placeService.getPlacesByType(anyString())).thenReturn(null);
	}

	@Test
	public void categorizeAndIndexPlacesTest(){
		List<Place> collections = Collections.emptyList();
		when(loaderService.categorizeAndIndexPlaces(collections)).thenReturn(null);
		doThrow(new RuntimeException()).when(loaderService).categorizeAndIndexPlaces(anyList());
		// maybe remove
		try {
			verify(loaderService.categorizeAndIndexPlaces(anyList())).contains(anyList());
		} catch (RuntimeException e){
			e.printStackTrace();
		}
	}
}
