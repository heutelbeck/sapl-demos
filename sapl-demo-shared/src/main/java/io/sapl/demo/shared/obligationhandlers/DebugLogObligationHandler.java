package io.sapl.demo.shared.obligationhandlers;

import io.sapl.spring.marshall.obligation.Obligation;
import io.sapl.spring.marshall.obligation.ObligationFailedException;
import io.sapl.spring.marshall.obligation.ObligationHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DebugLogObligationHandler implements ObligationHandler {

	private final boolean staticCanHandleResult;

	public DebugLogObligationHandler(boolean staticCanHandleResult) {
		this.staticCanHandleResult = staticCanHandleResult;
	}

	@Override
	public void handleObligation(Obligation obligation) throws ObligationFailedException {
		if (canHandle(obligation)) {
			LOGGER.debug("handled obligation with this logging. obligation: {}",
					obligation.getJsonObligation().toString());
		} else {
			LOGGER.debug("can not handle obligation. Will throw exception. obligation: {}",
					obligation.getJsonObligation().toString());
			throw new IllegalArgumentException("DebugLogObligationHandler was unable to handle the Obligation");
		}
	}

	@Override
	public boolean canHandle(Obligation obligation) {
		return staticCanHandleResult;
	}

}
