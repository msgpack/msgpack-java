package com.een.mpack;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "esn", "data", "timeout" })
public class Offer
{
	private int _esn;
	private long _timeout;
	private OfferData _data;
	
	public Offer()
	{
	}
	
	public int getEsn()
	{
		return this._esn;
	}
	
	public long getTimeout()
	{
		return this._timeout;
	}
	
	public OfferData getData()
	{
		return this._data;
	}
	
	public void setEsn( int _esn )
	{
		this._esn = _esn;
	}
	
	public void setTimeout( long _timeout )
	{
		this._timeout = _timeout;
	}
	
	public void setData( OfferData _data )
	{
		this._data = _data;
	}
	
	public boolean equals( final Object o )
	{
		if( o == this ) return true;
		if( !( o instanceof Offer ) ) return false;
		final Offer other = (Offer) o;
		if( !other.canEqual( (Object) this ) ) return false;
		if( this.getEsn() != other.getEsn() ) return false;
		if( this.getTimeout() != other.getTimeout() ) return false;
		final Object this$_data = this.getData();
		final Object other$_data = other.getData();
		if( this$_data == null ? other$_data != null : !this$_data.equals( other$_data ) ) return false;
		return true;
	}
	
	protected boolean canEqual( final Object other )
	{
		return other instanceof Offer;
	}
	
	public int hashCode()
	{
		final int PRIME = 59;
		int result = 1;
		result = result * PRIME + this.getEsn();
		final long $_timeout = this.getTimeout();
		result = result * PRIME + (int) ( $_timeout >>> 32 ^ $_timeout );
		final Object $_data = this.getData();
		result = result * PRIME + ( $_data == null ? 43 : $_data.hashCode() );
		return result;
	}
	
	public String toString()
	{
		return "Offer(_esn=" + this.getEsn() + ", _timeout=" + this.getTimeout() + ", _data=" + this.getData() + ")";
	}
}
