/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import com.jNotificationCenter.Notification;
import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author karl
 */
public class NotificationCenterTest {

    public NotificationCenterTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void testBasicObserver() throws InterruptedException {
        final NotificationCenterTest test = this;
        Notification.Center center = new Notification.Center();
        final ValueObject notificationRecieved = new ValueObject();
        center.addObserver(new Notification.Observer() {

            public void notify(Notification aNotification) {
                notificationRecieved.setValue(true);
            }
        },
                "TestNotification", null);


        center.postNotification(new Notification("TestNotification", this, null));
        Assert.assertTrue(notificationRecieved.getValue());
    }

    @Test
    public void testMultipleObservers() throws InterruptedException {
        final NotificationCenterTest test = this;
        Notification.Center center = new Notification.Center();
        final DualValueObject notificationRecieved = new DualValueObject();
        center.addObserver(new Notification.Observer() {

            public void notify(Notification aNotification) {
                notificationRecieved.setValue1(true);
            }
        },
                "TestNotification", null);

        center.addObserver(new Notification.Observer() {

            public void notify(Notification aNotification) {
                notificationRecieved.setValue2(true);
            }
        },
                "TestNotification", null);


        center.postNotification(new Notification("TestNotification", this, null));
        Assert.assertTrue(notificationRecieved.getValues());
    }

    @Test
    public void testOmnicientObserver() throws InterruptedException {
        final NotificationCenterTest test = this;
        Notification.Center center = new Notification.Center();
        final ValueObject notificationRecieved = new ValueObject();
        center.addObserver(new Notification.Observer() {

            public void notify(Notification aNotification) {
                notificationRecieved.setValue(true);
            }
        },
                null, null);


        center.postNotification(new Notification("TestNotification", this, null));
        Assert.assertTrue(notificationRecieved.getValue());
    }

    @Test
    public void notificationFromObject() throws InterruptedException {
        final NotificationCenterTest test = this;
        Notification.Center center = new Notification.Center();
        final ValueObject notificationRecieved = new ValueObject();
        center.addObserver(new Notification.Observer() {

            public void notify(Notification aNotification) {
                notificationRecieved.setValue(true);
            }
        },
                "TestNotification", test);


        center.postNotification(new Notification("TestNotification", this, null));
        Assert.assertTrue(notificationRecieved.getValue());
    }

    @Test
    public void omnicientObserverFromObject() throws InterruptedException {
        final NotificationCenterTest test = this;
        Notification.Center center = new Notification.Center();
        final ValueObject notificationRecieved = new ValueObject();
        center.addObserver(new Notification.Observer() {

            public void notify(Notification aNotification) {
                notificationRecieved.setValue(true);
            }
        },
                null, test);


        center.postNotification(new Notification("TestNotification", this, null));
        Assert.assertTrue(notificationRecieved.getValue());
    }

    @Test
    public void notificationCreatesObserver() {
        final NotificationCenterTest test = this;
        final Notification.Center center = new Notification.Center();
        final DualValueObject notificationRecieved = new DualValueObject();
        center.addObserver(new Notification.Observer() {

            public void notify(Notification aNotification) {
                notificationRecieved.setValue1(true);
                center.addObserver(new Notification.Observer() {

                    public void notify(Notification aNotification) {
                        notificationRecieved.setValue2(true);
                    }
                },
                        "TestNotification2", null);
            }
        },
                "TestNotification", null);




        center.postNotification(new Notification("TestNotification", this, null));
        Assert.assertFalse(notificationRecieved.getValues());

        center.postNotification(new Notification("TestNotification2", this, null));

        Assert.assertTrue(notificationRecieved.getValues());

        notificationRecieved.setValue1(false);
        notificationRecieved.setValue2(false);

        center.postNotification(new Notification("TestNotification", this, null));
        center.postNotification(new Notification("TestNotification2", this, null));
        Assert.assertTrue(notificationRecieved.getValues());
    }

    class ValueObject {

        boolean value;

        public void setValue(boolean value) {
            this.value = value;
        }

        public boolean getValue() {
            return value;
        }
    }

    class DualValueObject {

        boolean value;

        public void setValue1(boolean value) {
            this.value = value;
        }
        boolean value2;

        public void setValue2(boolean value) {
            this.value2 = value;
        }

        public boolean getValues() {
            return value && value2;
        }
    }
}
