import { useState } from "react";
import { Box, CircularProgress } from "@mui/material";
import Header from "../../components/Header";
import { motion } from "framer-motion";

const News = () => {
	const [infoLoaded, setInfoLoaded] = useState(false);

	if (infoLoaded === false) {
		return (
			<motion.div exit={{ opacity: 0 }}>
				<Box margin="1.5vh">
					<Header title="Currently under development"/>
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
				<Box display="flex" justifyContent="space-between" alignItems="center">
					<Header title="CS:GO News"/>
					<Box>

					</Box>
				</Box>
			</Box>
		</motion.div>
	);
};

export default News;