package com.cecurity;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;

public class MainApp {

    public static void main(String[] args) throws InterruptedException {

        OpenTelemetry openTelemetry = OpenTelemetryConfig.initOpenTelemetry();
        Tracer tracer = openTelemetry.getTracer("example-tracer");
        IO.println("Hello and welcome!");
        Span span = tracer.spanBuilder("main-operation").startSpan();
        for (int i = 1; i <= 25; i++) {
            try (Scope scope = span.makeCurrent()) {
                doWork(tracer,i);
            } catch (Exception e) {
                span.recordException(e);
            } finally {
                span.end();
            }

            // Give exporter time to flush
            Thread.sleep(200);
        }
        IO.println("Goodbye!");
    }

    private static void doWork(Tracer tracer, int i) {
        Span childSpan = tracer.spanBuilder("child-operation").startSpan();
        try {
            Thread.sleep(500);
            String info = String.format("Hello from --> %d", i);
            childSpan.setAttribute("custom.attribute", info);
        } catch (InterruptedException e) {
            childSpan.recordException(e);
        } finally {
            childSpan.end();
        }
    }
}
