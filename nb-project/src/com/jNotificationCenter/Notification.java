
package com.jNotificationCenter;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

//
//	===========================================================================
//
//	Title:		Notification.java
//	Description:	[Description]
//	Author:		Petite Abeille
//	Creation Date:	Fri 28-Jun-2002
//	Legal:		Copyright (C) 2001 Petite Abeille. All Rights Reserved.
//			This class is hereby released for all uses.
//			No warranties whatsoever.
//
//	---------------------------------------------------------------------------
//
import java.io.Externalizable;
import java.io.ObjectOutput;
import java.io.ObjectInput;
import java.io.IOException;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import java.util.Collection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.Iterator;

public final class Notification extends Object implements Externalizable, Comparable {

//	===========================================================================
//	Constant(s)
//	---------------------------------------------------------------------------
//	===========================================================================
//	Class variable(s)
//	---------------------------------------------------------------------------
//	===========================================================================
//	Instance variable(s)
//	---------------------------------------------------------------------------
    private String _name = null;
    private Object _object = null;
    private Map<String, Object> _userInfo = null;

//	===========================================================================
//	Constructor method(s)
//	---------------------------------------------------------------------------
    protected Notification() {
        super();
    }

    public Notification(String aName, Object anObject, Map<String, Object> anUserInfo) {
        this();

        if (aName != null) {
            this.setName(aName);
            this.setObject(anObject);
            this.setUserInfo(anUserInfo);
        } else {
            throw new IllegalArgumentException("Notification: null name.");
        }
    }

//	===========================================================================
//	Class method(s)
//	---------------------------------------------------------------------------
//	===========================================================================
//	Instance method(s)
//	---------------------------------------------------------------------------
    public String name() {
        return _name;
    }

    private void setName(String aValue) {
        _name = aValue;
    }

    public Object object() {
        return _object;
    }

    private void setObject(Object aValue) {
        _object = aValue;
    }

    public Map<String, Object> userInfo() {
        return _userInfo;
    }

    private void setUserInfo(Map<String, Object> aValue) {
        _userInfo = aValue;
    }

    @Override
    public int hashCode() {
        return this.name().hashCode();
    }

    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }

        if ((anObject instanceof Notification) == true) {
            Notification anotherNotification = (Notification) anObject;

            if (this.name().equals(anotherNotification.name()) == true) {
                Object aNotificationObject = this.object();
                Object anotherNotificationObject = anotherNotification.object();
                Map<String, Object> anUserInfo = this.userInfo();
                Map<String, Object> anotherUserInfo = anotherNotification.userInfo();

                if ((aNotificationObject != null)
                        && (aNotificationObject.equals(anotherNotificationObject) == false)) {
                    return false;
                }

                if ((anUserInfo != null)
                        && (anUserInfo.equals(anotherUserInfo) == false)) {
                    return false;
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return this.name();
    }

//	===========================================================================
//	Externalizable method(s)
//	---------------------------------------------------------------------------
    public void writeExternal(ObjectOutput anOutputStream) throws IOException {
        anOutputStream.writeObject(this.name());
        anOutputStream.writeObject(this.object());
        anOutputStream.writeObject(this.userInfo());
    }

    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput anInputStream) throws IOException, ClassNotFoundException {
        this.setName((String) anInputStream.readObject());
        this.setObject(anInputStream.readObject());
        this.setUserInfo((Map<String, Object>) anInputStream.readObject());
    }

//	===========================================================================
//	Comparable method(s)
//	---------------------------------------------------------------------------
    public int compareTo(Object anObject) {
        if (anObject != null) {
            if ((anObject instanceof Notification) == true) {
                return this.name().compareTo(((Notification) anObject).name());
            }

            throw new IllegalArgumentException("Notification.compareTo: '" + anObject.getClass().getName() + "' not instance of '" + Notification.class + "'.");
        }

        throw new IllegalArgumentException("Notification.compareTo: null object.");
    }

//	===========================================================================
//	Observer method(s)
//	---------------------------------------------------------------------------
    public static interface Observer {
        //note will be called on a separate thread
        public void notify(Notification aNotification);
    }

//	===========================================================================
//	Center method(s)
//	---------------------------------------------------------------------------
    public static final class Center extends Object {

        private static final Center _defaultCenter = new Center();
        private final Map<Object, Collection<Entry>> _map = new WeakHashMap<Object, Collection<Entry>>();

        public Center() {
            super();
        }

        public static Center defaultCenter() {
            return _defaultCenter;
        }

        private synchronized void registerObserverWithKey(Observer anObserver, Object aKey, String aName, Object anObject) {
            if (anObserver != null) {
                if (aKey != null) {
                    Collection<Entry> aCollection = _map.get(aKey);

                    if (aCollection == null) {
                        aCollection = new HashSet<Entry>();

                        _map.put(aKey, aCollection);
                    }

                    aCollection.add(new Entry(anObserver, aName, anObject));

                    return;
                }
            }

            throw new IllegalArgumentException("Notification.Center.registerObserverForNotificationName: null observer.");
        }

        public void addObserver(Observer anObserver, String aName, Object anObject) {
            if (anObserver != null) {
                if (aName != null) {
                    this.registerObserverWithKey(anObserver, aName, aName, anObject);
                }

                if (anObject != null) {
                    this.registerObserverWithKey(anObserver, anObject, aName, anObject);
                }

                if ((aName == null) && (anObject == null)) {
                    this.registerObserverWithKey(anObserver, Null.nullValue(), aName, anObject);
                }

                return;
            }

            throw new IllegalArgumentException("Notification.Center.addObserver: null observer.");
        }

        private synchronized void unregisterObserverWithKey(Observer anObserver, Object aKey, String aName, Object anObject) {
            if (anObserver != null) {
                if (aKey != null) {
                    Collection<Entry> aCollection = _map.get(aKey);

                    if ((aCollection != null) && (aCollection.isEmpty() == false)) {
                        Collection<Entry> someEntries = new ArrayList<Entry>();

                        for (Iterator<Entry> anIterator = aCollection.iterator(); anIterator.hasNext();) {
                            Entry anEntry = anIterator.next();

                            if ((anEntry.isValid() == false) || (anEntry.accepts(aName, anObject) != null)) {
                                someEntries.add(anEntry);
                            }
                        }

                        if (someEntries.isEmpty() == false) {
                            aCollection.removeAll(someEntries);
                        }
                    }

                    return;
                }
            }

            throw new IllegalArgumentException("Notification.Center.registerObserverForNotificationName: null observer.");
        }

        public void removeObserver(Observer anObserver, String aName, Object anObject) {
            if (anObserver != null) {
                if (aName != null) {
                    this.unregisterObserverWithKey(anObserver, aName, aName, anObject);
                }

                if (anObject != null) {
                    this.unregisterObserverWithKey(anObserver, anObject, aName, anObject);
                }

                if ((aName == null) && (anObject == null)) {
                    this.unregisterObserverWithKey(anObserver, Null.nullValue(), aName, anObject);
                }

                return;
            }

            throw new IllegalArgumentException("Notification.Center.removeObserver: null observer.");
        }

        private synchronized Collection<Observer> observersForKey(Object aKey, String aName, Object anObject) {
            if (aKey != null) {
                Collection<Entry> aCollection = _map.get(aKey);

                if (aCollection != null) {
                    Collection<Entry> someEntries = new ArrayList<Entry>();
                    Collection<Observer> someObservers = new ArrayList<Observer>();

                    for (Iterator anIterator = aCollection.iterator(); anIterator.hasNext();) {
                        Entry anEntry = (Entry) anIterator.next();

                        if (anEntry.isValid() == true) {
                            Observer anObserver = anEntry.accepts(aName, anObject);

                            if (anObserver != null) {
                                someObservers.add(anObserver);
                            }
                        } else {
                            someEntries.add(anEntry);
                        }
                    }

                    if (someEntries.isEmpty() == false) {
                        aCollection.removeAll(someEntries);
                    }

                    if (someObservers.isEmpty() == false) {
                        return someObservers;
                    }
                }

                return null;
            }

            throw new IllegalArgumentException("Notification.Center.observersForKey: null observer.");
        }

        public void postNotification(Notification aNotification) {
            if (aNotification != null) {
                String aName = aNotification.name();
                Object anObject = aNotification.object();
                Collection<Observer> aCollection = new HashSet<Observer>();

                if (aName != null) {
                    Collection<Observer> someObservers = this.observersForKey(aName, aName, anObject);

                    if (someObservers != null) {
                        aCollection.addAll(someObservers);
                    }
                }

                if (anObject != null) {
                    Collection<Observer> someObservers = this.observersForKey(anObject, aName, anObject);

                    if (someObservers != null) {
                        aCollection.addAll(someObservers);
                    }
                }

                {
                    Collection<Observer> someObservers = this.observersForKey(Null.nullValue(), aName, anObject);

                    if (someObservers != null) {
                        aCollection.addAll(someObservers);
                    }
                }

                if (aCollection.isEmpty() == false) {
//                    Task aTask = new Task(aCollection, aNotification);
//                    Thread aThread = new Thread(aTask, aTask.getClass().getName());
//
//                    aThread.setDaemon(true);
//                    aThread.setPriority(Thread.MIN_PRIORITY);
//                    aThread.start();
                    for(Observer observer : aCollection){
                        observer.notify(aNotification);
                    }


                }

                return;
            }

            throw new IllegalArgumentException("Notification.Center.postNotification: null notification.");
        }
    }

//	===========================================================================
//	Entry method(s)
//	---------------------------------------------------------------------------
    private static final class Entry extends Object {

        private Reference _observer = null;
        private String _name = null;
        private Reference _object = null;

        private Entry(Observer anObserver, String aName, Object anObject) {
            super();

            this.setObserver(anObserver);
            this.setName(aName);
            this.setObject(anObject);
        }

        private Observer observer() {
            if (_observer != null) {
                return (Observer) _observer.get();
            }

            return null;
        }

        private void setObserver(Observer aValue) {
            if (aValue != null) {
                _observer = new WeakReference<Observer>(aValue);
            }
        }

        private String name() {
            return _name;
        }

        private void setName(String aValue) {
            _name = aValue;
        }

        private Object object() {
            if (_object != null) {
                return _object.get();
            }

            return null;
        }

        private void setObject(Object aValue) {
            if (aValue != null) {
                _object = new WeakReference<Object>(aValue);
            }
        }

        private boolean isValid() {
            Observer anObserver = this.observer();

            if (anObserver != null) {
                return true;
            }

            return false;
        }

        private Observer accepts(String aName, Object anObject) {
            Observer anObserver = this.observer();

            if (anObserver != null) {
                String anEntryName = this.name();
                Object anEntryObject = this.object();

                if ((anEntryName == null) && (anEntryObject == null)) {
                    return anObserver;
                } else if ((anEntryName != null) && (anEntryObject != null)) {
                    if ((anEntryName.equals(aName) == true)
                            && (anEntryObject.equals(anObject) == true)) {
                        return anObserver;
                    }
                } else if (anEntryName != null) {
                    if (anEntryName.equals(aName) == true) {
                        return anObserver;
                    }
                } else if (anEntryObject != null) {
                    if (anEntryObject.equals(anObject) == true) {
                        return anObserver;
                    }
                }
            }

            return null;
        }
    }

//	===========================================================================
//	Null Object
//	---------------------------------------------------------------------------
    private static final class Null extends Object {

        private static Object nullValue = new Object();

        public static Object nullValue() {
            return nullValue;
        }
    }

//	===========================================================================
//	Task method(s)
//	---------------------------------------------------------------------------
    private static final class Task extends Object implements Runnable {

        private Collection<Observer> _observers = null;
        private Notification _notification = null;

        private Task(Collection<Observer> someObservers, Notification aNotification) {
            super();

            this.setObservers(someObservers);
            this.setNotification(aNotification);
        }

        private Collection<Observer> observers() {
            return _observers;
        }

        private void setObservers(Collection<Observer> aValue) {
            _observers = aValue;
        }

        private Notification notification() {
            return _notification;
        }

        private void setNotification(Notification aValue) {
            _notification = aValue;
        }

        public void run() {
            Notification aNotification = this.notification();

            for (Iterator<Observer> anIterator = this.observers().iterator(); anIterator.hasNext();) {
                Observer anObserver = anIterator.next();

                anObserver.notify(aNotification);
            }
        }
    }
}
