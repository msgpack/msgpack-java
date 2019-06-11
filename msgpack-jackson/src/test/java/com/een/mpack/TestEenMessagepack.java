package com.een.mpack;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.msgpack.jackson.dataformat.MessagePackFactory;

public class TestEenMessagepack
{
	
	@Test
	public void testOfferDeserializeVms() throws Exception
	{
		byte[] mpackBytes = getTestBytes( "offer-by-vms.mpack" );
		
		Offer output = getMessagePackMapper().readValue( mpackBytes, Offer.class );
		
		assertThat( output, is( equalTo( getOfferTestObj() ) ) );
	}
	
	@Test
	public void testOfferDeserializeCM() throws Exception
	{
		byte[] mpackBytes = getTestBytes( "offer-by-cm.mpack" );
		
		Offer output = getMessagePackMapper().readValue( mpackBytes, Offer.class );
		
		// This test uses the same object to compare against as testOfferDeserializeVms as vms encoded the timeout as
		// signed 16 bit int while this lib encoded it as unsigned 16 bit int which are equivalent with the current value.
		assertThat( output, is( equalTo( getOfferTestObj() ) ) );
	}
	
	@Test
	public void testOfferSerialize() throws Exception
	{
		Offer offer = getOfferTestObj();
		byte[] expected = getTestBytes( "offer-by-cm.mpack" );
		
		byte[] output = getMessagePackMapper().writeValueAsBytes( offer );
		
		String expectedString = toByteString( expected );
		String outputString = toByteString( output );
		
		// String makes it easier to see differences in hex, but we do check the two byte arrays directly afterwards.
		assertThat( outputString, is( equalTo( expectedString ) ) );
		assertThat( output, is( equalTo( expected ) ) );
	}
	
	private static String toByteString( final byte[] data )
	{
		return toByteString( data, 0, data.length );
	}
	
	private static String toByteString( final byte[] data, final int offset, final int length )
	{
		final StringBuilder sb = new StringBuilder();
		final int end = offset + length;
		
		for( int i = offset; i < end && i < data.length; )
		{
			final String s = Integer.toHexString( data[i] );
			
			if( s.length() == 1 )
				sb.append( "0" ).append( s );
			else if( s.length() == 2 )
				sb.append( s );
			else
				sb.append( s.substring( s.length() - 2 ) );
			
			i++;
			
			if( i < end )
				sb.append( " " );
		}
		
		return sb.toString().toUpperCase( Locale.ENGLISH );
	}
	
	private ObjectMapper getMessagePackMapper()
	{
		return new ObjectMapper( new MessagePackFactory() );
	}
	
	private Offer getOfferTestObj() throws IOException
	{
		URL offerJsonUrl = TestEenMessagepack.class.getClassLoader().getResource( "./testdata/offer.json" );
		
		assertThat( offerJsonUrl, is( notNullValue() ) );
		
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue( offerJsonUrl, Offer.class );
	}
	
	private byte[] getTestBytes( String filename ) throws IOException
	{
		try( InputStream inputStream = TestEenMessagepack.class.getResourceAsStream( "/testdata/" + filename ) )
		{
			return IOUtils.toByteArray( inputStream );
		}
	}
}
