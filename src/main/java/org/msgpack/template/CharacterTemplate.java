package org.msgpack.template;

import java.io.IOException;
import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;

/**
 * CharacterTemplate<br/>
 *
 * @author  watabiki
 */
public class CharacterTemplate extends AbstractTemplate<Character> {

    private CharacterTemplate() { }

    @Override
    public void write(Packer pk, Character v, boolean required) throws IOException {
        if (v == null) {
            if (required) {
                throw new MessageTypeException("Attempted to write null");
            }
            pk.writeNil();
            return;
        }
        pk.writeInt(v.charValue());
    }

    @Override
    public Character read(Unpacker u, Character to, boolean required) throws IOException {
        if (!required && u.trySkipNil()) {
            return null;
        }
        return  (char) u.readInt();
    }

    static public CharacterTemplate getInstance() {
        return instance;
    }

    static final CharacterTemplate instance = new CharacterTemplate();
}
