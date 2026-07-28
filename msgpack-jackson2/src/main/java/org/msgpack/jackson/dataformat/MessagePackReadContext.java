package org.msgpack.jackson.dataformat;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.io.CharTypes;
import com.fasterxml.jackson.core.io.ContentReference;
import com.fasterxml.jackson.core.json.DupDetector;

/**
 * Replacement of {@link com.fasterxml.jackson.core.json.JsonReadContext}
 * to support features needed by MessagePack format.
 */
public final class MessagePackReadContext
    extends JsonStreamContext
{
    /**
     * Parent context for this context; null for root context.
     */
    protected final MessagePackReadContext parent;

    // // // Optional duplicate detection

    protected final DupDetector dups;

    /**
     * For fixed-size Arrays, Objects, this indicates expected number of entries.
     */
    protected int expEntryCount;

    // // // Location information (minus source reference)

    protected String currentName;

    protected Object currentValue;

    /*
    /**********************************************************
    /* Simple instance reuse slots
    /**********************************************************
     */

    protected MessagePackReadContext child = null;

    /*
    /**********************************************************
    /* Instance construction, reuse
    /**********************************************************
     */

    public MessagePackReadContext(MessagePackReadContext parent, DupDetector dups,
                                  int type, int expEntryCount)
    {
        super();
        this.parent = parent;
        this.dups = dups;
        _type = type;
        this.expEntryCount = expEntryCount;
        _index = -1;
        _nestingDepth = parent == null ? 0 : parent._nestingDepth + 1;
    }

    protected void reset(int type, int expEntryCount)
    {
        _type = type;
        this.expEntryCount = expEntryCount;
        _index = -1;
        currentName = null;
        currentValue = null;
        if (dups != null) {
            dups.reset();
        }
    }

    @Override
    public Object getCurrentValue()
    {
        return currentValue;
    }

    @Override
    public void setCurrentValue(Object v)
    {
        currentValue = v;
    }

    // // // Factory methods

    public static MessagePackReadContext createRootContext(DupDetector dups)
    {
        return new MessagePackReadContext(null, dups, TYPE_ROOT, -1);
    }

    public MessagePackReadContext createChildArrayContext(int expEntryCount)
    {
        MessagePackReadContext ctxt = child;
        if (ctxt == null) {
            ctxt = new MessagePackReadContext(this,
                    (dups == null) ? null : dups.child(),
                            TYPE_ARRAY, expEntryCount);
            child = ctxt;
        }
        else {
            ctxt.reset(TYPE_ARRAY, expEntryCount);
        }
        return ctxt;
    }

    public MessagePackReadContext createChildObjectContext(int expEntryCount)
    {
        MessagePackReadContext ctxt = child;
        if (ctxt == null) {
            ctxt = new MessagePackReadContext(this,
                    (dups == null) ? null : dups.child(),
                    TYPE_OBJECT, expEntryCount);
            child = ctxt;
            return ctxt;
        }
        ctxt.reset(TYPE_OBJECT, expEntryCount);
        return ctxt;
    }

    /*
    /**********************************************************
    /* Abstract method implementation
    /**********************************************************
     */

    @Override
    public String getCurrentName()
    {
        return currentName;
    }

    @Override
    public MessagePackReadContext getParent()
    {
        return parent;
    }

    /*
    /**********************************************************
    /* Extended API
    /**********************************************************
     */

    public boolean hasExpectedLength()
    {
        return (expEntryCount >= 0);
    }

    public int getExpectedLength()
    {
        return expEntryCount;
    }

    public boolean isEmpty()
    {
        return expEntryCount == 0;
    }

    public int getRemainingExpectedLength()
    {
        int diff = expEntryCount - _index;
        // Negative values would occur when expected count is -1
        return Math.max(0, diff);
    }

    public boolean acceptsBreakMarker()
    {
        return (expEntryCount < 0) && _type != TYPE_ROOT;
    }

    /**
     * Method called to increment the current entry count (Object property, Array
     * element or Root value) for this context level
     * and then see if more entries are accepted.
     * The only case where more entries are NOT expected is for fixed-count
     * Objects and Arrays that just reached the entry count.
     *<p>
     * Note that since the entry count is updated this is a state-changing method.
     */
    public boolean expectMoreValues()
    {
        if (++_index == expEntryCount) {
            return false;
        }
        return true;
    }

    /**
     * @return Location pointing to the point where the context
     *   start marker was found
     */
    @Override
    public JsonLocation startLocation(ContentReference srcRef)
    {
        return new JsonLocation(srcRef, 1L, -1, -1);
    }

    @Override
    @Deprecated // since 2.13
    public JsonLocation getStartLocation(Object rawSrc)
    {
        return startLocation(ContentReference.rawReference(rawSrc));
    }

    /*
    /**********************************************************
    /* State changes
    /**********************************************************
     */

    public void setCurrentName(String name) throws JsonProcessingException
    {
        currentName = name;
        if (dups != null) {
            _checkDup(dups, name);
        }
    }

    private void _checkDup(DupDetector dd, String name) throws JsonProcessingException
    {
        if (dd.isDup(name)) {
            throw new JsonParseException(null,
                    "Duplicate field '" + name + "'", dd.findLocation());
        }
    }

    /*
    /**********************************************************
    /* Overridden standard methods
    /**********************************************************
     */

    /**
     * Overridden to provide developer readable "JsonPath" representation
     * of the context.
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder(64);
        switch (_type) {
        case TYPE_ROOT:
            sb.append("/");
            break;
        case TYPE_ARRAY:
            sb.append('[');
            sb.append(getCurrentIndex());
            sb.append(']');
            break;
        case TYPE_OBJECT:
            sb.append('{');
            if (currentName != null) {
                sb.append('"');
                CharTypes.appendQuoted(sb, currentName);
                sb.append('"');
            }
            else {
                sb.append('?');
            }
            sb.append('}');
            break;
        }
        return sb.toString();
    }
}
