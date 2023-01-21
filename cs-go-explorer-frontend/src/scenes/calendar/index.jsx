import { useState, useEffect } from "react";
import { Box, CircularProgress, List, ListItem, ListItemText, Typography, useTheme } from "@mui/material";
import FullCalendar from "@fullcalendar/react";
import { formatDate } from '@fullcalendar/core'
import dayGridPlugin from "@fullcalendar/daygrid";
import timeGridPlugin from "@fullcalendar/timegrid";
import interactionPlugin from "@fullcalendar/interaction";
import listPlugin from "@fullcalendar/list";
import { motion } from "framer-motion";
import { tokens } from "../../theme";
import Header from "../../components/Header";
import SidebarBackgroundImage from "../../images/backgrounds/calendar_events_background.jpg";

const Calendar = () => {
	const theme = useTheme();
	const colors = tokens(theme.palette.mode);
	const [infoLoaded, setInfoLoaded] = useState(false);
	const [currentEvents, setCurrentEvents] = useState([]);

	// Setting local storage for current events
	useEffect(() => {
		const storedCurrentEvents = JSON.parse(localStorage.getItem("current_events"));
		if (storedCurrentEvents)
			setCurrentEvents(storedCurrentEvents);

		setInfoLoaded(true);
	}, []);

	useEffect(() => {
		localStorage.setItem("current_events", JSON.stringify(currentEvents));
	}, [currentEvents]);

	const handleDateClick = (selected) => {
		const title = prompt("Please enter a new title for your event");
		const calendarApi = selected.view.calendar;
		calendarApi.unselect();
		if (title) {
			calendarApi.addEvent({
				id: `${selected.dateStr}-${title}`,
				title,
				start: selected.startStr,
				end: selected.endStr,
				allDay: selected.allDay,
			});
		}
	};

	const handleEventClick = (selected) => {
		if (window.confirm(`Are you sure you want to delete the event '${selected.event.title}'`))
			selected.event.remove();
	};

	if (infoLoaded === false) {
		return (
			<motion.div exit={{ opacity: 0 }}>
				<Box margin="1.5vh">
					<Box sx={{ position: 'absolute', left: '50%', top: '50%', transform: 'translate(-50%, -50%)' }}>
						<CircularProgress color="success"/>
					</Box>
				</Box>
			</motion.div>
		);
	}
	return (
		<motion.div exit={{ opacity: 0 }}>
			<Box margin="1.5vh">
				<Header title="Calendar" subtitle="Set the events you are planning in CS:GO"/>
				<Box display="flex" justifyContent="space-between">
					{/* CALENDAR SIDEBAR */}
					<Box
						flex="1 1 20%"
						padding="15px"
						borderRadius="5px"
						sx={{
							backgroundImage: `url(${SidebarBackgroundImage}) !important`,
							backgroundSize: 'cover',
							backgroundRepeat  : 'no-repeat',
							backgroundPosition: 'center',
						}}
					>
						<Typography variant="h5">Events</Typography>
						<List>
							{currentEvents.map((event) => (
								<ListItem
									key={event.id}
									sx={{
										backgroundColor: "custom.steamColorF",
										margin: "10px 0",
										borderRadius: "2px"
									}}
								>
									<ListItemText
										primary={event.title}
										secondary={
											<Typography color="custom.steamColorA">
												{formatDate(event.start, {
													year: "numeric",
													month: "short",
													day: "numeric"
												})}
											</Typography>
										}
									/>
								</ListItem>
							))}
						</List>
					</Box>

					{/* CALENDAR */}
					<Box flex="1 1 100%" marginLeft="15px">
						<FullCalendar
							height="75vh"
							plugins={[
								dayGridPlugin,
								timeGridPlugin,
								interactionPlugin,
								listPlugin
							]}
							headerToolbar={{
								left: "prev,next today",
								center: "title",
								right: "dayGridMonth,timeGridWeek,timeGridDay,listMonth"
							}}
							eventBackgroundColor={colors.steamColors[5]}
							eventTextColor={colors.steamColors[1]}
							initialView="dayGridMonth"
							editable={true}
							selectable={true}
							selectMirror={true}
							dayMaxEvents={true}
							select={handleDateClick}
							eventClick={handleEventClick}
							eventsSet={(events) => setCurrentEvents(events)}
							initialEvents={currentEvents}
						/>
					</Box>
				</Box>
			</Box>
		</motion.div>
	);
}

export default Calendar;