import { Box } from "@mui/material";
import Header from "../../components/Header";
import PieChart from "../../components/PieChart";
import { motion } from "framer-motion";

const Pie = () => {
	return (
		<motion.div exit={{ opacity: 0 }}>
			<Box m="20px">
				<Header title="Pie Chart" subtitle="Simple Pie Chart"/>
				<Box height="75vh">
					<PieChart/>
				</Box>
			</Box>
		</motion.div>
	);
};

export default Pie;