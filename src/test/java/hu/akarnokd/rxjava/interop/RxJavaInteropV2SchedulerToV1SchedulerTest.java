package hu.akarnokd.rxjava.interop;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.TestScheduler;
import org.junit.Test;
import rx.functions.Action0;
import rx.internal.schedulers.SchedulerLifecycle;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RxJavaInteropV2SchedulerToV1SchedulerTest {

    @Test
    public void now() {
        Scheduler v2Scheduler = mock(Scheduler.class);
        rx.Scheduler v1Scheduler = RxJavaInterop.toV1Scheduler(v2Scheduler);

        when(v2Scheduler.now(MILLISECONDS)).thenReturn(123L);

        assertEquals(123L, v1Scheduler.now());
    }

    @Test
    public void workerSchedule() {
        TestScheduler v2Scheduler = new TestScheduler();
        rx.Scheduler v1Scheduler = RxJavaInterop.toV1Scheduler(v2Scheduler);

        Action0 action0 = mock(Action0.class);

        v1Scheduler.createWorker().schedule(action0);
        verifyZeroInteractions(action0);

        v2Scheduler.triggerActions();
        verify(action0).call();
    }

    @Test
    public void workerScheduleNullAction() {
        Scheduler v2Scheduler = mock(Scheduler.class);
        rx.Scheduler v1Scheduler = RxJavaInterop.toV1Scheduler(v2Scheduler);

        try {
            v1Scheduler.createWorker().schedule(null);
            fail();
        } catch (NullPointerException expected) {
            assertEquals("Source 1.x Action0 is null", expected.getMessage());
        }
    }

    @Test
    public void workerScheduleDelayed() {
        TestScheduler v2Scheduler = new TestScheduler();
        rx.Scheduler v1Scheduler = RxJavaInterop.toV1Scheduler(v2Scheduler);

        Action0 action0 = mock(Action0.class);

        v1Scheduler.createWorker().schedule(action0, 123L, MINUTES);
        verifyZeroInteractions(action0);

        v2Scheduler.advanceTimeBy(122L, MINUTES);
        verifyZeroInteractions(action0);

        v2Scheduler.advanceTimeBy(1L, MINUTES);
        verify(action0).call();

        v2Scheduler.advanceTimeBy(125L, MINUTES); // Check that it's not periodic.
        verifyNoMoreInteractions(action0);
    }

    @Test
    public void workerScheduleDelayedNullAction() {
        Scheduler v2Scheduler = mock(Scheduler.class);
        rx.Scheduler v1Scheduler = RxJavaInterop.toV1Scheduler(v2Scheduler);

        try {
            v1Scheduler.createWorker().schedule(null, 123L, MINUTES);
            fail();
        } catch (NullPointerException expected) {
            assertEquals("Source 1.x Action0 is null", expected.getMessage());
        }
    }

    @Test
    public void workerSchedulePeriodically() {
        TestScheduler v2Scheduler = new TestScheduler();
        rx.Scheduler v1Scheduler = RxJavaInterop.toV1Scheduler(v2Scheduler);

        Action0 action0 = mock(Action0.class);

        v1Scheduler.createWorker().schedulePeriodically(action0, 10L, 123L, MINUTES);
        verifyZeroInteractions(action0);

        v2Scheduler.advanceTimeBy(9L, MINUTES);
        verifyZeroInteractions(action0);

        v2Scheduler.advanceTimeBy(1L, MINUTES);
        verify(action0).call();

        v2Scheduler.advanceTimeBy(122L, MINUTES);
        verifyNoMoreInteractions(action0);

        v2Scheduler.advanceTimeBy(1L, MINUTES);
        verify(action0, times(2)).call();

        v2Scheduler.advanceTimeBy(123L, MINUTES);
        verify(action0, times(3)).call(); // Verify periodic.

        v2Scheduler.advanceTimeBy(123L, MINUTES);
        verify(action0, times(4)).call(); // Verify periodic.
    }

    @Test
    public void workerSchedulePeriodicallyNullAction() {
        Scheduler v2Scheduler = mock(Scheduler.class);
        rx.Scheduler v1Scheduler = RxJavaInterop.toV1Scheduler(v2Scheduler);

        try {
            v1Scheduler.createWorker().schedulePeriodically(null, 10L, 123L, MINUTES);
            fail();
        } catch (NullPointerException expected) {
            assertEquals("Source 1.x Action0 is null", expected.getMessage());
        }
    }

    @Test
    public void workerNow() {
        Scheduler v2Scheduler = mock(Scheduler.class);
        rx.Scheduler v1Scheduler = RxJavaInterop.toV1Scheduler(v2Scheduler);

        Scheduler.Worker v2Worker = mock(Scheduler.Worker.class);
        when(v2Scheduler.createWorker()).thenReturn(v2Worker);
        rx.Scheduler.Worker v1Worker = v1Scheduler.createWorker();

        when(v2Worker.now(MILLISECONDS)).thenReturn(123L);

        assertEquals(123L, v1Worker.now());
    }

    @Test
    public void workerUnsubscribe() {
        Scheduler v2Scheduler = mock(Scheduler.class);
        rx.Scheduler v1Scheduler = RxJavaInterop.toV1Scheduler(v2Scheduler);

        Scheduler.Worker v2Worker = mock(Scheduler.Worker.class);
        when(v2Scheduler.createWorker()).thenReturn(v2Worker);

        rx.Scheduler.Worker v1Worker = v1Scheduler.createWorker();
        verify(v2Worker, never()).dispose();

        v1Worker.unsubscribe();
        verify(v2Worker).dispose();
    }

    @Test
    public void workerIsUnsubscribed() {
        Scheduler v2Scheduler = mock(Scheduler.class);
        rx.Scheduler v1Scheduler = RxJavaInterop.toV1Scheduler(v2Scheduler);

        Scheduler.Worker v2Worker = mock(Scheduler.Worker.class);
        when(v2Scheduler.createWorker()).thenReturn(v2Worker);

        rx.Scheduler.Worker v1Worker = v1Scheduler.createWorker();

        when(v2Worker.isDisposed()).thenReturn(true);
        assertTrue(v1Worker.isUnsubscribed());

        when(v2Worker.isDisposed()).thenReturn(false);
        assertFalse(v1Worker.isUnsubscribed());
    }

    @Test
    public void startStopSupport() {
        Scheduler v2Scheduler = mock(Scheduler.class);
        rx.Scheduler v1Scheduler = RxJavaInterop.toV1Scheduler(v2Scheduler);

        SchedulerLifecycle lc = (SchedulerLifecycle)v1Scheduler;

        lc.start();

        verify(v2Scheduler).start();

        lc.shutdown();

        verify(v2Scheduler).shutdown();
    }
}
