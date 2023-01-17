import { Box } from "@mui/material";
import Header from "../../components/Header";
import LineChart from "../../components/LineChart";
import { motion } from "framer-motion";

const Line = () => {
	return (
		<motion.div exit={{ opacity: 0 }}>
			<Box m="20px">
				<Header title="Line Chart" subtitle="Simple Line Chart"/>
				<Box height="75vh">
					<LineChart/>
				</Box>
			</Box>
		</motion.div>
	);
};

export default Line;