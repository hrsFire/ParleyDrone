package at.rhomberg.manager;

import java.util.Hashtable;

public class IntentHelper {

    private static IntentHelper _instance;
    private Hashtable<String, Object> _hash;

    private void InstantHelper() {
        _hash = new Hashtable<String, Object>();
    }

    private static IntentHelper getInstance() {
        if( _instance == null)
            _instance = new IntentHelper();
        return _instance;
    }

    public static void addObjectForKey( Object object, String key) {
        getInstance()._hash.put( key, object);
    }

    public static Object getObjectFromKey( String key) {
        Object object = getInstance()._hash.get(key);
        getInstance()._hash.remove(key);
        return object;
    }
}
