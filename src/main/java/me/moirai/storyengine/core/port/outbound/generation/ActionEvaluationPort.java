package me.moirai.storyengine.core.port.outbound.generation;

public interface ActionEvaluationPort {

    ActionEvaluationResult evaluateAction(ActionEvaluationRequest request);
}
