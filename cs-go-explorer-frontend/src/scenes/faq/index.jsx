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
				<Header title="FAQ" subtitle="Frequently Asked Questions Page"/>
				<Accordion>
					<AccordionSummary expandIcon={<ExpandMoreIcon />}>
						<Typography color="custom.steamColorF" variant="h5">
							An Important Question
						</Typography>
					</AccordionSummary>
					<AccordionDetails>
						<Typography color="custom.steamColorD">
							Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse
							malesuada lacus ex, sit amet blandit leo lobortis eget.
						</Typography>
					</AccordionDetails>
				</Accordion>
				<Accordion>
					<AccordionSummary expandIcon={<ExpandMoreIcon />}>
						<Typography color="custom.steamColorF" variant="h5">
							Another Important Question
						</Typography>
					</AccordionSummary>
					<AccordionDetails>
						<Typography color="custom.steamColorD">
							Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse
							malesuada lacus ex, sit amet blandit leo lobortis eget.
						</Typography>
					</AccordionDetails>
				</Accordion>
				<Accordion>
					<AccordionSummary expandIcon={<ExpandMoreIcon />}>
						<Typography color="custom.steamColorF" variant="h5">
							Your Favorite Question
						</Typography>
					</AccordionSummary>
					<AccordionDetails>
						<Typography color="custom.steamColorD">
							Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse
							malesuada lacus ex, sit amet blandit leo lobortis eget.
						</Typography>
					</AccordionDetails>
				</Accordion>
				<Accordion>
					<AccordionSummary expandIcon={<ExpandMoreIcon />}>
						<Typography color="custom.steamColorF" variant="h5">
							Some Random Question
						</Typography>
					</AccordionSummary>
					<AccordionDetails>
						<Typography color="custom.steamColorD">
							Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse
							malesuada lacus ex, sit amet blandit leo lobortis eget.
						</Typography>
					</AccordionDetails>
				</Accordion>
				<Accordion>
					<AccordionSummary expandIcon={<ExpandMoreIcon />}>
						<Typography color="custom.steamColorF" variant="h5">
							The Final Question
						</Typography>
					</AccordionSummary>
					<AccordionDetails>
						<Typography color="custom.steamColorD">
							Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse
							malesuada lacus ex, sit amet blandit leo lobortis eget.
						</Typography>
					</AccordionDetails>
				</Accordion>
			</Box>
		</motion.div>
	);
};

export default FAQ;