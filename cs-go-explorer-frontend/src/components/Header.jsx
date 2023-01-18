import { Typography, Box } from '@mui/material';

const Header = ({ title, subtitle }) => {

	return (
		<Box marginBottom="2.5vh">
			<Typography
				variant="h2"
				color="custom.steamColorD"
				fontWeight="bold"
			>
				{title}
			</Typography>
			<Typography variant="h5" color="custom.steamColorF">
				{subtitle}
			</Typography>
		</Box>
	);
}

export default Header;