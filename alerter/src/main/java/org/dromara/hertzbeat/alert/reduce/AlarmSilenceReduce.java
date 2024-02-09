package org.dromara.hertzbeat.alert.reduce;

import lombok.RequiredArgsConstructor;
import org.dromara.hertzbeat.alert.dao.AlertSilenceDao;
import org.dromara.hertzbeat.common.cache.CacheFactory;
import org.dromara.hertzbeat.common.cache.ICacheService;
import org.dromara.hertzbeat.common.constants.CommonConstants;
import org.dromara.hertzbeat.common.entity.alerter.Alert;
import org.dromara.hertzbeat.common.entity.alerter.AlertSilence;
import org.dromara.hertzbeat.common.entity.manager.TagItem;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlarmSilenceReduce {

	private final AlertSilenceDao alertSilenceDao;

	public boolean filterSilence(Alert alert) {
		List<AlertSilence> alertSilenceList = getAlertSilenceList();
		for (AlertSilence alertSilence : alertSilenceList) {
			if (!alertSilence.isEnable()) {
				continue;
			}
			if (isAlertSilenced(alert, alertSilence)) {
				return false;
			}
		}
		return true;
	}

	private List<AlertSilence> getAlertSilenceList() {
		ICacheService<String, Object> silenceCache = CacheFactory.getAlertSilenceCache();
		List<AlertSilence> alertSilenceList = (List<AlertSilence>) silenceCache.get(CommonConstants.CACHE_ALERT_SILENCE);
		if (alertSilenceList == null) {
			alertSilenceList = alertSilenceDao.findAll();
			silenceCache.put(CommonConstants.CACHE_ALERT_SILENCE, alertSilenceList);
		}
		return alertSilenceList;
	}

	private boolean isAlertSilenced(Alert alert, AlertSilence alertSilence) {
		if (matchesAlertSilenceRules(alert, alertSilence) && withinSilencePeriod(alertSilence)) {
			incrementSilenceTimes(alertSilence);
			return true;
		}
		return false;
	}

	private boolean matchesAlertSilenceRules(Alert alert, AlertSilence alertSilence) {
		boolean match = alertSilence.isMatchAll();
		if (!match) {
			match = matchesTagsAndPriorities(alert, alertSilence);
		}
		return match;
	}

	private boolean matchesTagsAndPriorities(Alert alert, AlertSilence alertSilence) {
		boolean match = matchTags(alert, alertSilence);
		if (match && alertSilence.getPriorities() != null && !alertSilence.getPriorities().isEmpty()) {
			match = alertSilence.getPriorities().stream().anyMatch(priority -> priority.equals(alert.getPriority()));
		}
		return match;
	}

	private boolean matchTags(Alert alert, AlertSilence alertSilence) {
		if (alert.getTags() != null && !alert.getTags().isEmpty()) {
			Map<String, String> alertTagMap = alert.getTags();
			return alertSilence.getTags().stream().anyMatch(item ->
					matchTag(item, alertTagMap));
		}
		return true;
	}

	private boolean matchTag(TagItem item, Map<String, String> alertTagMap) {
		String tagValue = alertTagMap.get(item.getName());
		return tagValue == null ? item.getValue() == null : tagValue.equals(item.getValue());
	}

	private boolean withinSilencePeriod(AlertSilence alertSilence) {
		LocalDateTime now = LocalDateTime.now();
		if (alertSilence.getType() == 0) {
			return isWithinOnceTimePeriod(now, alertSilence);
		} else if (alertSilence.getType() == 1) {
			return isWithinCyclicTimePeriod(now, alertSilence);
		}
		return false;
	}

	private boolean isWithinOnceTimePeriod(LocalDateTime now, AlertSilence alertSilence) {
		return (alertSilence.getPeriodStart() == null || now.isAfter(alertSilence.getPeriodStart().toLocalDateTime()))
				&& (alertSilence.getPeriodEnd() == null || now.isBefore(alertSilence.getPeriodEnd().toLocalDateTime()));
	}

	private boolean isWithinCyclicTimePeriod(LocalDateTime now, AlertSilence alertSilence) {
		int currentDayOfWeek = now.toLocalDate().getDayOfWeek().getValue();
		if (alertSilence.getDays() != null && alertSilence.getDays().contains(currentDayOfWeek)) {
			LocalTime nowTime = now.toLocalTime();
			return (alertSilence.getPeriodStart() == null || nowTime.isAfter(alertSilence.getPeriodStart().toLocalTime()))
					&& (alertSilence.getPeriodEnd() == null || nowTime.isBefore(alertSilence.getPeriodEnd().toLocalTime()));
		}
		return false;
	}

	private void incrementSilenceTimes(AlertSilence alertSilence) {
		int times = Optional.ofNullable(alertSilence.getTimes()).orElse(0);
		alertSilence.setTimes(times + 1);
		alertSilenceDao.save(alertSilence);
	}
}
