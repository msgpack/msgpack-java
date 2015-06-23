package org.msgpack.jackson.dataformat;

/**
 * Created by komamitsu on 5/28/15.
 */
public class Tuple<F, S>
{
    private final F first;
    private final S second;

    public Tuple(F first, S second)
    {
        this.first = first;
        this.second = second;
    }

    public F first()
    {
        return first;
    }

    public S second()
    {
        return second;
    }
}
