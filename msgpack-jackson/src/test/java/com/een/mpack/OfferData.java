package com.een.mpack;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "url", "esn", "session" })
public class OfferData
{
	private int _esn;
	private String _url;
	private String _session;
	
	public OfferData()
	{
	}
	
	public int getEsn()
	{
		return this._esn;
	}
	
	public String getUrl()
	{
		return this._url;
	}
	
	public String getSession()
	{
		return this._session;
	}
	
	public void setEsn( int _esn )
	{
		this._esn = _esn;
	}
	
	public void setUrl( String _url )
	{
		this._url = _url;
	}
	
	public void setSession( String _session )
	{
		this._session = _session;
	}
	
	public boolean equals( final Object o )
	{
		if( o == this ) return true;
		if( !( o instanceof OfferData ) ) return false;
		final OfferData other = (OfferData) o;
		if( !other.canEqual( (Object) this ) ) return false;
		if( this.getEsn() != other.getEsn() ) return false;
		final Object this$_url = this.getUrl();
		final Object other$_url = other.getUrl();
		if( this$_url == null ? other$_url != null : !this$_url.equals( other$_url ) ) return false;
		final Object this$_session = this.getSession();
		final Object other$_session = other.getSession();
		if( this$_session == null ? other$_session != null : !this$_session.equals( other$_session ) ) return false;
		return true;
	}
	
	protected boolean canEqual( final Object other )
	{
		return other instanceof OfferData;
	}
	
	public int hashCode()
	{
		final int PRIME = 59;
		int result = 1;
		result = result * PRIME + this.getEsn();
		final Object $_url = this.getUrl();
		result = result * PRIME + ( $_url == null ? 43 : $_url.hashCode() );
		final Object $_session = this.getSession();
		result = result * PRIME + ( $_session == null ? 43 : $_session.hashCode() );
		return result;
	}
	
	public String toString()
	{
		return "OfferData(_esn=" + this.getEsn() + ", _url=" + this.getUrl() + ", _session=" + this.getSession() + ")";
	}
}
