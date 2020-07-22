package com.example.demo.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Entity.MpxFile;
import com.example.demo.Entity.User;
import com.example.demo.Repository.FileRepository;
import com.example.demo.Repository.UserRepository;

@Service
public class FileServiceImpl implements FileService {

	@Autowired
	private FileRepository fileRepo;

	@Autowired
	private UserRepository userRepo;

	private String FILEPATH = "./.temp/";

	@Override
	public int uploadFile(MultipartFile file, String username) {

		File tempFileDirectory = new File(FILEPATH);
		File pdfDirectory = new File("./pdf/");
		if (!tempFileDirectory.isDirectory()) {
			tempFileDirectory.mkdir();
			pdfDirectory.mkdir();
		}
		User user = userRepo.findByUsername(username).get();
		for (MpxFile mpxFile : user.getFiles()) {
			if (mpxFile.getName().equals(file.getOriginalFilename())) {
				return 0;
			}
		}

		try {

			File newDirectory = new File(FILEPATH, username);
			String filepath = FILEPATH + username + "/" + file.getOriginalFilename();
			if (!newDirectory.isDirectory()) {
				newDirectory.mkdir();
			}

			byte[] bytes = file.getBytes();
			Path path = Paths.get(filepath);
			Files.write(path, bytes);

			Set<MpxFile> files = new HashSet<MpxFile>();

			MpxFile n = new MpxFile();

			n.setLocation(filepath);
			n.setName(file.getOriginalFilename());
			n.setUser(user);

			if (user.getFiles() == null) {
				user.setFiles(files);
			}
			user.getFiles().add(n);

			userRepo.save(user);

			return 1;

		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public void deleteFile(MpxFile file) {

		File storedFile = new File(file.getLocation());
		User user = file.getUser();
		user.removeMpxFile(file);
		userRepo.save(user);

		storedFile.delete();

		file.removeProject();

		fileRepo.save(file);
		fileRepo.delete(file);

	}

}