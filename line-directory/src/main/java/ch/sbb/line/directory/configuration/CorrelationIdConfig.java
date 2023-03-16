package ch.sbb.line.directory.configuration;

import static org.springframework.boot.actuate.autoconfigure.tracing.MicrometerTracingAutoConfiguration.RECEIVER_TRACING_OBSERVATION_HANDLER_ORDER;
import static org.springframework.boot.actuate.autoconfigure.tracing.MicrometerTracingAutoConfiguration.SENDER_TRACING_OBSERVATION_HANDLER_ORDER;

import ch.sbb.atlas.configuration.filter.CorrelationIdFilterConfig;
import io.micrometer.observation.Observation;
import io.micrometer.observation.transport.ReceiverContext;
import io.micrometer.observation.transport.SenderContext;
import io.micrometer.tracing.CurrentTraceContext;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.handler.DefaultTracingObservationHandler;
import io.micrometer.tracing.handler.PropagatingReceiverTracingObservationHandler;
import io.micrometer.tracing.handler.PropagatingSenderTracingObservationHandler;
import io.micrometer.tracing.handler.TracingObservationHandler.TracingContext;
import io.micrometer.tracing.propagation.Propagator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;

@Configuration
@Import(CorrelationIdFilterConfig.class)
public class CorrelationIdConfig {

  @Bean
  public DefaultTracingObservationHandler defaultTracingObservationHandler(Tracer tracer) {
    return new DefaultTracingObservationHandler(tracer) {
      @Override
      public void onScopeClosed(Observation.Context context) {
        TracingContext tracingContext = getTracingContext(context);
        CurrentTraceContext.Scope scope = tracingContext.getScope();
        if (scope != null) {
          scope.close();
        }
      }
    };
  }

  @Bean
  @Order(SENDER_TRACING_OBSERVATION_HANDLER_ORDER)
  public PropagatingSenderTracingObservationHandler<?> propagatingSenderTracingObservationHandler(Tracer tracer, Propagator propagator) {
    return new PropagatingSenderTracingObservationHandler<>(tracer, propagator) {
      @Override
      public void onScopeClosed(SenderContext context) {
        TracingContext tracingContext = getTracingContext(context);
        CurrentTraceContext.Scope scope = tracingContext.getScope();
        if (scope != null) {
          scope.close();
        }
      }
    };
  }

  @Bean
  @Order(RECEIVER_TRACING_OBSERVATION_HANDLER_ORDER)
  public PropagatingReceiverTracingObservationHandler<?> propagatingReceiverTracingObservationHandler(Tracer tracer, Propagator propagator) {
    return new PropagatingReceiverTracingObservationHandler<>(tracer, propagator) {
      @Override
      public void onScopeClosed(ReceiverContext context) {
        TracingContext tracingContext = getTracingContext(context);
        CurrentTraceContext.Scope scope = tracingContext.getScope();
        if (scope != null) {
          scope.close();
        }
      }
    };
  }
}
