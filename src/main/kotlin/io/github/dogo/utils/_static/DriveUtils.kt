package io.github.dogo.utils._static

import com.google.api.client.googleapis.media.MediaHttpUploader
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener
import com.google.api.client.http.InputStreamContent
import io.github.dogo.core.DogoBot
import java.io.File

class DriveUtils {
    companion object {

        /**
         * Uploads a file to Google Drive.
         * The upload is done in a separated thread.
         *
         * @param[file] the file
         */
        fun toDrive(file: File, parentId: String? = null): com.google.api.services.drive.model.File {
            if (!file.exists() || file.isDirectory && file.length() > 0) throw Exception("Invalid File")
            val mediaContent = InputStreamContent("text/plain", file.inputStream())
            val fileMetadata = com.google.api.services.drive.model.File().apply {
                name = file.name
                parentId?.let {
                    parents = listOf(parentId)
                }
            }
            return DogoBot.googleDrive.files().create(fileMetadata, mediaContent).apply {
                mediaHttpUploader.isDirectUploadEnabled = false
                mediaHttpUploader.chunkSize = MediaHttpUploader.MINIMUM_CHUNK_SIZE
                mediaHttpUploader.progressListener = object: MediaHttpUploaderProgressListener {
                    override fun progressChanged(uploader: MediaHttpUploader?) {
                        when(uploader?.uploadState){
                            MediaHttpUploader.UploadState.INITIATION_STARTED -> {
                                DogoBot.logger.info("Starting upload for ${file.name}")
                            }
                            MediaHttpUploader.UploadState.INITIATION_COMPLETE -> {
                                DogoBot.logger.info("Upload for ${file.name} started!")
                            }
                            MediaHttpUploader.UploadState.MEDIA_IN_PROGRESS -> {
                                DogoBot.logger.info("Upload for ${file.name}: ${uploader.progress}%")
                            }
                            MediaHttpUploader.UploadState.MEDIA_COMPLETE -> {
                                DogoBot.logger.info("Upload for ${file.name} is done!")
                            }
                            MediaHttpUploader.UploadState.NOT_STARTED -> {
                                DogoBot.logger.info("Upload for ${file.name}: NOT_STARTED")
                            }
                        }
                    }
                }
                fields = "id, webContentLink, webViewLink, parents"
            }.execute()
        }


        /**
         * Creates a directory.
         *
         * @param[dirName] the directory name
         * @param[parentId] the directory parent
         */
        fun mkdir(dirName: String, parentId: String? = null): com.google.api.services.drive.model.File {
            return DogoBot.googleDrive.files().create(com.google.api.services.drive.model.File().apply {
                name = dirName
                mimeType = "application/vnd.google-apps.folder"
                parentId?.let {
                    parents = listOf(parentId)
                }
            }).execute()
        }

        /**
         * Gets a directory or a list of directories
         *
         * @param[dirName] the name to query.
         * @param[parentId] the id of the parent directory. Null means 'root'.
         */
        fun getDir(dirName: String, parentId: String? = null): List<com.google.api.services.drive.model.File> {
            val query = " name = '$dirName'  and mimeType = 'application/vnd.google-apps.folder'  and '${parentId ?: "root"}' in parents"
            val list = mutableListOf<com.google.api.services.drive.model.File>()
            var pageToken: String?

            do {
                pageToken = DogoBot.googleDrive.files().list().apply {
                    q = query
                    spaces = "drive"
                    fields = "nextPageToken, files(id, name, createdTime)"
                    pageToken = null
                }.execute().apply {
                    list.addAll(files)
                }.nextPageToken
            } while (pageToken != null)

            return list
        }
    }
}