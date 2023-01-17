import { Box, useTheme } from "@mui/material";
import Header from "../../components/Header";
import Accordion from "@mui/material/Accordion";
import AccordionSummary from "@mui/material/AccordionSummary";
import AccordionDetails from "@mui/material/AccordionDetails";
import Typography from "@mui/material/Typography";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import { tokens } from "../../theme";
import { motion } from "framer-motion";

const FAQ = () => {
	const theme = useTheme();
	const colors = tokens(theme.palette.mode);

	return (
		<motion.div exit={{ opacity: 0 }}>
			<Box m="20px">
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
				<Accordion sx={{marginTop: "2%"}}>
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
				<Accordion sx={{marginTop: "2%"}}>
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
				<Accordion sx={{marginTop: "2%"}}>
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
				<Accordion sx={{marginTop: "2%"}}>
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