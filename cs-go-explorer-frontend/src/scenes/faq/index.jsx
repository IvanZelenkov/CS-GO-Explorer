import { Box } from "@mui/material";
import Header from "../../components/Header";
import Accordion from "@mui/material/Accordion";
import AccordionSummary from "@mui/material/AccordionSummary";
import AccordionDetails from "@mui/material/AccordionDetails";
import Typography from "@mui/material/Typography";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import { motion } from "framer-motion";

const FAQ = () => {
	return (
		<motion.div exit={{ opacity: 0 }}>
			<Box margin="1.5vh">
				<Header title="FAQ" subtitle="Frequently Asked Questions"/>
				<Accordion>
					<AccordionSummary expandIcon={<ExpandMoreIcon/>}>
						<Typography sx={{
							color: "custom.steamColorD",
							fontSize: "1.4vh",
							fontFamily: "Montserrat"
						}}>
							What is the purpose of the CS:GO Explorer application?
						</Typography>
					</AccordionSummary>
					<AccordionDetails>
						<Typography sx={{
							color: "custom.steamColorF",
							fontSize: "1.3vh",
							fontFamily: "Montserrat"
						}}>
							A CS:GO Explorer app designed to help you keep track of stats in detail,
							and view your friends' Steam accounts. You can see a lot of different
							information about your friends, including the Steam ID, which you can
							copy to see their stats and compare with yours. The statistics will
							appear in a form of tables, bar charts, and pie charts. Every time you
							play a match, look at the latest stats on the Profile page. Given
							information will help you define where the skills should be improved.
						</Typography>
					</AccordionDetails>
				</Accordion>
				<Accordion>
					<AccordionSummary expandIcon={<ExpandMoreIcon/>}>
						<Typography sx={{
							color: "custom.steamColorD",
							fontSize: "1.4vh",
							fontFamily: "Montserrat"
						}}>
							What is the purpose of the Calendar feature?
						</Typography>
					</AccordionSummary>
					<AccordionDetails>
						<Typography sx={{
							color: "custom.steamColorF",
							fontSize: "1.3vh",
							fontFamily: "Montserrat"
						}}>
							The calendar can be used to mark important events you plan to attend,
							such as the day and time you play with teammates or practice, or you
							can set the time when certain eSports teams play in a tournament.
						</Typography>
					</AccordionDetails>
				</Accordion>
				<Accordion>
					<AccordionSummary expandIcon={<ExpandMoreIcon/>}>
						<Typography sx={{
							color: "custom.steamColorD",
							fontSize: "1.4vh",
							fontFamily: "Montserrat"
						}}>
							How often stats are updated?
						</Typography>
					</AccordionSummary>
					<AccordionDetails>
						<Typography sx={{
							color: "custom.steamColorF",
							fontSize: "1.3vh",
							fontFamily: "Montserrat"
						}}>
							Any of your actions in a match is instantly transmitted to the CS:GO Explorer.
						</Typography>
					</AccordionDetails>
				</Accordion>
				<Accordion>
					<AccordionSummary expandIcon={<ExpandMoreIcon/>}>
						<Typography sx={{
							color: "custom.steamColorD",
							fontSize: "1.4vh",
							fontFamily: "Montserrat"
						}}>
							How do I provide feedback about CS:GO Explorer?
						</Typography>
					</AccordionSummary>
					<AccordionDetails>
						<Typography sx={{
							color: "custom.steamColorF",
							fontSize: "1.3vh",
							fontFamily: "Montserrat"
						}}>
							Write to itproger.ivan@gmail.com for feedback.
						</Typography>
					</AccordionDetails>
				</Accordion>
			</Box>
		</motion.div>
	);
};

export default FAQ;