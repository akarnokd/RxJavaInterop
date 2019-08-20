package hu.akarnokd.rxjava3.interop;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import hu.akarnokd.rxjava3.interop.RxJavaInterop;
import rx.internal.schedulers.SchedulerLifecycle;

public class RxJavaInteropV1SchedulerToV3SchedulerTest {

    @Test
    public void now() {
        rx.Scheduler v1Scheduler = mock(rx.Scheduler.class);
        io.reactivex.rxjava3.core.Scheduler v3Scheduler = RxJavaInterop.toV3Scheduler(v1Scheduler);

        when(v1Scheduler.now()).thenReturn(123L);

        assertEquals(123L, v3Scheduler.now(TimeUnit.MILLISECONDS));
    }

    @Test
    public void workerSchedule() {
        rx.schedulers.TestScheduler v1Scheduler = new rx.schedulers.TestScheduler();
        io.reactivex.rxjava3.core.Scheduler v3Scheduler = RxJavaInterop.toV3Scheduler(v1Scheduler);

        Runnable action0 = mock(Runnable.class);

        v3Scheduler.createWorker().schedule(action0);
        verifyZeroInteractions(action0);

        v1Scheduler.triggerActions();
        verify(action0).run();
    }

    @Test
    public void workerScheduleNullAction() {
        rx.Scheduler v1Scheduler = mock(rx.Scheduler.class);
        io.reactivex.rxjava3.core.Scheduler v3Scheduler = RxJavaInterop.toV3Scheduler(v1Scheduler);

        try {
            v3Scheduler.createWorker().schedule(null);
            fail();
        } catch (NullPointerException expected) {
            assertEquals("Source 3.x Runnable is null", expected.getMessage());
        }
    }

    @Test
    public void workerScheduleDelayed() {
        rx.schedulers.TestScheduler v1Scheduler = new rx.schedulers.TestScheduler();
        io.reactivex.rxjava3.core.Scheduler v3Scheduler = RxJavaInterop.toV3Scheduler(v1Scheduler);

        Runnable action0 = mock(Runnable.class);

        v3Scheduler.createWorker().schedule(action0, 123L, MINUTES);
        verifyZeroInteractions(action0);

        v1Scheduler.advanceTimeBy(122L, MINUTES);
        verifyZeroInteractions(action0);

        v1Scheduler.advanceTimeBy(1L, MINUTES);
        verify(action0).run();

        v1Scheduler.advanceTimeBy(125L, MINUTES); // Check that it's not periodic.
        verifyNoMoreInteractions(action0);
    }

    @Test
    public void workerScheduleDelayedNullAction() {
        rx.Scheduler v1Scheduler = mock(rx.Scheduler.class);
        io.reactivex.rxjava3.core.Scheduler v3Scheduler = RxJavaInterop.toV3Scheduler(v1Scheduler);

        try {
            v3Scheduler.createWorker().schedule(null, 123L, MINUTES);
            fail();
        } catch (NullPointerException expected) {
            assertEquals("Source 3.x Runnable is null", expected.getMessage());
        }
    }

    @Test
    public void workerSchedulePeriodically() {
        rx.schedulers.TestScheduler v1Scheduler = new rx.schedulers.TestScheduler();
        io.reactivex.rxjava3.core.Scheduler v3Scheduler = RxJavaInterop.toV3Scheduler(v1Scheduler);

        Runnable action0 = mock(Runnable.class);

        v3Scheduler.createWorker().schedulePeriodically(action0, 10L, 123L, MINUTES);
        verifyZeroInteractions(action0);

        v1Scheduler.advanceTimeBy(9L, MINUTES);
        verifyZeroInteractions(action0);

        v1Scheduler.advanceTimeBy(1L, MINUTES);
        verify(action0).run();

        v1Scheduler.advanceTimeBy(122L, MINUTES);
        verifyNoMoreInteractions(action0);

        v1Scheduler.advanceTimeBy(1L, MINUTES);
        verify(action0, times(2)).run();

        v1Scheduler.advanceTimeBy(123L, MINUTES);
        verify(action0, times(3)).run(); // Verify periodic.

        v1Scheduler.advanceTimeBy(123L, MINUTES);
        verify(action0, times(4)).run(); // Verify periodic.
    }

    @Test
    public void workerSchedulePeriodicallyNullAction() {
        rx.Scheduler v1Scheduler = mock(rx.Scheduler.class);
        io.reactivex.rxjava3.core.Scheduler v3Scheduler = RxJavaInterop.toV3Scheduler(v1Scheduler);

        try {
            v3Scheduler.createWorker().schedulePeriodically(null, 10L, 123L, MINUTES);
            fail();
        } catch (NullPointerException expected) {
            assertEquals("Source 3.x Runnable is null", expected.getMessage());
        }
    }

    @Test
    public void workerNow() {
        rx.Scheduler v1Scheduler = mock(rx.Scheduler.class);
        io.reactivex.rxjava3.core.Scheduler v3Scheduler = RxJavaInterop.toV3Scheduler(v1Scheduler);

        rx.Scheduler.Worker v1Worker = mock(rx.Scheduler.Worker.class);
        when(v1Scheduler.createWorker()).thenReturn(v1Worker);
        io.reactivex.rxjava3.core.Scheduler.Worker v3Worker = v3Scheduler.createWorker();

        when(v1Worker.now()).thenReturn(123L);

        assertEquals(123L, v3Worker.now(TimeUnit.MILLISECONDS));
    }

    @Test
    public void workerUnsubscribe() {
        rx.Scheduler v1Scheduler = mock(rx.Scheduler.class);
        io.reactivex.rxjava3.core.Scheduler v3Scheduler = RxJavaInterop.toV3Scheduler(v1Scheduler);

        rx.Scheduler.Worker v1Worker = mock(rx.Scheduler.Worker.class);
        when(v1Scheduler.createWorker()).thenReturn(v1Worker);

        io.reactivex.rxjava3.core.Scheduler.Worker v3Worker = v3Scheduler.createWorker();
        verify(v1Worker, never()).unsubscribe();

        v3Worker.dispose();
        verify(v1Worker).unsubscribe();
    }

    @Test
    public void workerIsUnsubscribed() {
        rx.Scheduler v1Scheduler = mock(rx.Scheduler.class);
        io.reactivex.rxjava3.core.Scheduler v3Scheduler = RxJavaInterop.toV3Scheduler(v1Scheduler);

        rx.Scheduler.Worker v1Worker = mock(rx.Scheduler.Worker.class);
        when(v1Scheduler.createWorker()).thenReturn(v1Worker);

        io.reactivex.rxjava3.core.Scheduler.Worker v3Worker = v3Scheduler.createWorker();

        when(v1Worker.isUnsubscribed()).thenReturn(true);
        assertTrue(v3Worker.isDisposed());

        when(v1Worker.isUnsubscribed()).thenReturn(false);
        assertFalse(v3Worker.isDisposed());
    }

    @Test
    public void startStopNotSupported() {
        rx.Scheduler v1Scheduler = mock(rx.Scheduler.class);
        io.reactivex.rxjava3.core.Scheduler v3Scheduler = RxJavaInterop.toV3Scheduler(v1Scheduler);

        v3Scheduler.start();

        verifyNoMoreInteractions(v1Scheduler);

        v3Scheduler.shutdown();

        verifyNoMoreInteractions(v1Scheduler);
    }

    @Test
    public void startStopSupported() {
        rx.Scheduler v1Scheduler = mock(rx.Scheduler.class, withSettings().extraInterfaces(SchedulerLifecycle.class));
        io.reactivex.rxjava3.core.Scheduler v3Scheduler = RxJavaInterop.toV3Scheduler(v1Scheduler);

        v3Scheduler.start();

        ((SchedulerLifecycle)verify(v1Scheduler)).start();

        v3Scheduler.shutdown();

        ((SchedulerLifecycle)verify(v1Scheduler)).shutdown();
    }
}
