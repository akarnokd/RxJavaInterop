package hu.akarnokd.rxjava3.interop;

import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.TestScheduler;
import org.junit.Test;

import hu.akarnokd.rxjava3.interop.RxJavaInterop;
import rx.functions.Action0;
import rx.internal.schedulers.SchedulerLifecycle;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RxJavaInteropV3SchedulerToV1SchedulerTest {

    @Test
    public void now() {
        Scheduler v3Scheduler = mock(Scheduler.class);
        rx.Scheduler v1Scheduler = RxJavaInterop.toV1Scheduler(v3Scheduler);

        when(v3Scheduler.now(MILLISECONDS)).thenReturn(123L);

        assertEquals(123L, v1Scheduler.now());
    }

    @Test
    public void workerSchedule() {
        TestScheduler v3Scheduler = new TestScheduler();
        rx.Scheduler v1Scheduler = RxJavaInterop.toV1Scheduler(v3Scheduler);

        Action0 action0 = mock(Action0.class);

        v1Scheduler.createWorker().schedule(action0);
        verifyNoInteractions(action0);

        v3Scheduler.triggerActions();
        verify(action0).call();
    }

    @Test
    public void workerScheduleNullAction() {
        Scheduler v3Scheduler = mock(Scheduler.class);
        rx.Scheduler v1Scheduler = RxJavaInterop.toV1Scheduler(v3Scheduler);

        try {
            v1Scheduler.createWorker().schedule(null);
            fail();
        } catch (NullPointerException expected) {
            assertEquals("Source 1.x Action0 is null", expected.getMessage());
        }
    }

    @Test
    public void workerScheduleDelayed() {
        TestScheduler v3Scheduler = new TestScheduler();
        rx.Scheduler v1Scheduler = RxJavaInterop.toV1Scheduler(v3Scheduler);

        Action0 action0 = mock(Action0.class);

        v1Scheduler.createWorker().schedule(action0, 123L, MINUTES);
        verifyNoInteractions(action0);

        v3Scheduler.advanceTimeBy(122L, MINUTES);
        verifyNoInteractions(action0);

        v3Scheduler.advanceTimeBy(1L, MINUTES);
        verify(action0).call();

        v3Scheduler.advanceTimeBy(125L, MINUTES); // Check that it's not periodic.
        verifyNoMoreInteractions(action0);
    }

    @Test
    public void workerScheduleDelayedNullAction() {
        Scheduler v3Scheduler = mock(Scheduler.class);
        rx.Scheduler v1Scheduler = RxJavaInterop.toV1Scheduler(v3Scheduler);

        try {
            v1Scheduler.createWorker().schedule(null, 123L, MINUTES);
            fail();
        } catch (NullPointerException expected) {
            assertEquals("Source 1.x Action0 is null", expected.getMessage());
        }
    }

    @Test
    public void workerSchedulePeriodically() {
        TestScheduler v3Scheduler = new TestScheduler();
        rx.Scheduler v1Scheduler = RxJavaInterop.toV1Scheduler(v3Scheduler);

        Action0 action0 = mock(Action0.class);

        v1Scheduler.createWorker().schedulePeriodically(action0, 10L, 123L, MINUTES);
        verifyNoInteractions(action0);

        v3Scheduler.advanceTimeBy(9L, MINUTES);
        verifyNoInteractions(action0);

        v3Scheduler.advanceTimeBy(1L, MINUTES);
        verify(action0).call();

        v3Scheduler.advanceTimeBy(122L, MINUTES);
        verifyNoMoreInteractions(action0);

        v3Scheduler.advanceTimeBy(1L, MINUTES);
        verify(action0, times(2)).call();

        v3Scheduler.advanceTimeBy(123L, MINUTES);
        verify(action0, times(3)).call(); // Verify periodic.

        v3Scheduler.advanceTimeBy(123L, MINUTES);
        verify(action0, times(4)).call(); // Verify periodic.
    }

    @Test
    public void workerSchedulePeriodicallyNullAction() {
        Scheduler v3Scheduler = mock(Scheduler.class);
        rx.Scheduler v1Scheduler = RxJavaInterop.toV1Scheduler(v3Scheduler);

        try {
            v1Scheduler.createWorker().schedulePeriodically(null, 10L, 123L, MINUTES);
            fail();
        } catch (NullPointerException expected) {
            assertEquals("Source 1.x Action0 is null", expected.getMessage());
        }
    }

    @Test
    public void workerNow() {
        Scheduler v3Scheduler = mock(Scheduler.class);
        rx.Scheduler v1Scheduler = RxJavaInterop.toV1Scheduler(v3Scheduler);

        Scheduler.Worker v3Worker = mock(Scheduler.Worker.class);
        when(v3Scheduler.createWorker()).thenReturn(v3Worker);
        rx.Scheduler.Worker v1Worker = v1Scheduler.createWorker();

        when(v3Worker.now(MILLISECONDS)).thenReturn(123L);

        assertEquals(123L, v1Worker.now());
    }

    @Test
    public void workerUnsubscribe() {
        Scheduler v3Scheduler = mock(Scheduler.class);
        rx.Scheduler v1Scheduler = RxJavaInterop.toV1Scheduler(v3Scheduler);

        Scheduler.Worker v3Worker = mock(Scheduler.Worker.class);
        when(v3Scheduler.createWorker()).thenReturn(v3Worker);

        rx.Scheduler.Worker v1Worker = v1Scheduler.createWorker();
        verify(v3Worker, never()).dispose();

        v1Worker.unsubscribe();
        verify(v3Worker).dispose();
    }

    @Test
    public void workerIsUnsubscribed() {
        Scheduler v3Scheduler = mock(Scheduler.class);
        rx.Scheduler v1Scheduler = RxJavaInterop.toV1Scheduler(v3Scheduler);

        Scheduler.Worker v3Worker = mock(Scheduler.Worker.class);
        when(v3Scheduler.createWorker()).thenReturn(v3Worker);

        rx.Scheduler.Worker v1Worker = v1Scheduler.createWorker();

        when(v3Worker.isDisposed()).thenReturn(true);
        assertTrue(v1Worker.isUnsubscribed());

        when(v3Worker.isDisposed()).thenReturn(false);
        assertFalse(v1Worker.isUnsubscribed());
    }

    @Test
    public void startStopSupport() {
        Scheduler v3Scheduler = mock(Scheduler.class);
        rx.Scheduler v1Scheduler = RxJavaInterop.toV1Scheduler(v3Scheduler);

        SchedulerLifecycle lc = (SchedulerLifecycle)v1Scheduler;

        lc.start();

        verify(v3Scheduler).start();

        lc.shutdown();

        verify(v3Scheduler).shutdown();
    }
}
